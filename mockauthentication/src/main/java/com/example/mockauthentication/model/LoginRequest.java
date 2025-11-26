package com.example.mockauthentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequest {

    private static final Logger logger = LoggerFactory.getLogger(LoginRequest.class);

    private String type;
    private String clid;
    private String sec;

    @JsonProperty("bank_id") // ✅ Maps JSON key "bank_id" → field bankId
    private String bankId;

    private String username;
    private String password;

    @JsonProperty("ip_address") // ✅ Maps JSON key "ip_address" → field ipAddress
    private String ipAddress;

    private Abc abc;

    // Constructor — optional logging
    public LoginRequest() {
        logger.debug("🆕 LoginRequest object created.");
    }

    // ✅ Nested Abc class
    public static class Abc {
        private static final Logger logger = LoggerFactory.getLogger(Abc.class);

        private String VERSION;
        private Abp abp;
        private String MAC;
        private String ExternalIP;

        public String getVERSION() { return VERSION; }
        public void setVERSION(String VERSION) {
            this.VERSION = VERSION;
            logger.debug("📦 Abc.VERSION set to: {}", VERSION);
        }

        public Abp getAbp() { return abp; }
        public void setAbp(Abp abp) {
            this.abp = abp;
            logger.debug("🔗 Abc.abp object set.");
        }

        public String getMAC() { return MAC; }
        public void setMAC(String MAC) {
            this.MAC = MAC;
            logger.debug("💻 Abc.MAC set to: {}", MAC);
        }

        public String getExternalIP() { return ExternalIP; }
        public void setExternalIP(String externalIP) {
            this.ExternalIP = externalIP;
            logger.debug("🌐 Abc.ExternalIP set to: {}", externalIP);
        }
    }

    // ✅ Nested Abp class
    public static class Abp {
        private static final Logger logger = LoggerFactory.getLogger(Abp.class);

        private SystemInfo System;
        private Browser Browser;

        public SystemInfo getSystem() { return System; }
        public void setSystem(SystemInfo system) {
            this.System = system;
            logger.debug("🧩 Abp.System object set.");
        }

        public Browser getBrowser() { return Browser; }
        public void setBrowser(Browser browser) {
            this.Browser = browser;
            logger.debug("🌍 Abp.Browser object set.");
        }
    }

    // ✅ Nested SystemInfo class
    public static class SystemInfo {
        private static final Logger logger = LoggerFactory.getLogger(SystemInfo.class);

        private String Platform;
        private String Language;

        public String getPlatform() { return Platform; }
        public void setPlatform(String platform) {
            this.Platform = platform;
            logger.debug("🖥️ System.Platform set to: {}", platform);
        }

        public String getLanguage() { return Language; }
        public void setLanguage(String language) {
            this.Language = language;
            logger.debug("🗣️ System.Language set to: {}", language);
        }
    }

    // ✅ Nested Browser class
    public static class Browser {
        private static final Logger logger = LoggerFactory.getLogger(Browser.class);

        private String UserAgent;

        public String getUserAgent() { return UserAgent; }
        public void setUserAgent(String userAgent) {
            this.UserAgent = userAgent;
            logger.debug("🌐 Browser.UserAgent set to: {}", userAgent);
        }
    }

    // ✅ Getters & Setters for Main Fields
    public String getType() { return type; }
    public void setType(String type) {
        this.type = type;
        logger.debug("📘 Type set to: {}", type);
    }

    public String getClid() { return clid; }
    public void setClid(String clid) {
        this.clid = clid;
        logger.debug("🆔 Clid set to: {}", clid);
    }

    public String getSec() { return sec; }
    public void setSec(String sec) {
        this.sec = sec;
        logger.debug("🔑 Sec set to: {}", sec);
    }

    public String getBankId() { return bankId; }
    public void setBankId(String bankId) {
        this.bankId = bankId;
        logger.debug("🏦 BankId set to: {}", bankId);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
        logger.debug("👤 Username set to: {}", username);
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        logger.debug("🔒 Password set (hidden for security).");
    }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        logger.debug("📡 IP Address set to: {}", ipAddress);
    }

    public Abc getAbc() { return abc; }
    public void setAbc(Abc abc) {
        this.abc = abc;
        logger.debug("🧱 Abc object set inside LoginRequest.");
    }
}
