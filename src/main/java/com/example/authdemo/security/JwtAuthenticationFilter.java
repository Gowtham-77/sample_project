package com.example.authdemo.security;

import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.repository.UserRepository;
import com.example.authdemo.service.JwtService;
import com.example.authdemo.service.RedisService;
import com.example.authdemo.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RedisService redisService;
    private final UserRepository userRepository; // ✅ To fetch role from DB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        log.debug("🔹 Incoming request: {} with Authorization: {}", requestURI, authHeader);

        // ✅ Step 1: Skip if no Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username;

        // ✅ Step 2: Extract username from token
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            log.warn("❌ Invalid JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Step 3: Check Redis blacklist
        if (redisService.isTokenBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token is blacklisted. Please login again.");
            return;
        }

        // ✅ Step 4: Authenticate only if context is empty
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Step 5: Fetch user role directly from DB (always trusted)
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("⚠️ User '{}' not found in DB", username);
                filterChain.doFilter(request, response);
                return;
            }

            UserEntity user = userOpt.get();
            String actualRole = user.getRole();

            // ✅ Always ensure "ROLE_" prefix
            if (!actualRole.startsWith("ROLE_")) {
                actualRole = "ROLE_" + actualRole;
            }

            var authorities = Collections.singletonList(new SimpleGrantedAuthority(actualRole));

            // ✅ Step 6: Validate token
            if (jwtService.isTokenValid(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("✅ JWT validated for user: {} with role from DB: {}", username, actualRole);
            } else {
                log.warn("❌ Invalid or expired JWT token for user: {}", username);
            }
        }

        // ✅ Step 7: Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
