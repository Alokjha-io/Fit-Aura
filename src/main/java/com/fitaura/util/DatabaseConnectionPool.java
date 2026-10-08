package com.fitaura.util;

import com.fitaura.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Thread-safe singleton connection pool provider for FitAura using HikariCP.
 * Manages an application-level HikariDataSource, loading configuration
 * dynamically from environment variables with fallback to application.properties.
 */
public final class DatabaseConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    // Environment variable keys
    public static final String ENV_DB_URL = "FITAURA_DB_URL";
    public static final String ENV_DB_USERNAME = "FITAURA_DB_USERNAME";
    public static final String ENV_DB_PASSWORD = "FITAURA_DB_PASSWORD";
    public static final String ENV_DB_MAX_POOL_SIZE = "FITAURA_DB_MAX_POOL_SIZE";
    public static final String ENV_DB_MIN_IDLE = "FITAURA_DB_MIN_IDLE";
    public static final String ENV_DB_IDLE_TIMEOUT = "FITAURA_DB_IDLE_TIMEOUT";
    public static final String ENV_DB_CONNECTION_TIMEOUT = "FITAURA_DB_CONNECTION_TIMEOUT";

    // Defaults
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_JDBC_URL =
            "jdbc:mysql://localhost:3306/fitaura?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static volatile HikariDataSource dataSource;
    private static final Object lock = new Object();

    private DatabaseConnectionPool() {
        // Prevent direct instantiation
    }

    /**
     * Retrieves or initializes the shared HikariDataSource.
     */
    public static HikariDataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (lock) {
                if (dataSource == null || dataSource.isClosed()) {
                    initializeDataSource();
                }
            }
        }
        return dataSource;
    }

    /**
     * Initializes the HikariCP pool from environment variables or application.properties.
     */
    private static void initializeDataSource() {
        String password = null;
        try {
            Properties props = loadProperties();

            // Resolve URL: Env Var -> properties -> default
            String jdbcUrl = getEnvOrProp(new String[]{ENV_DB_URL, "DB_URL"}, props, "db.url", DEFAULT_JDBC_URL);
            String username = getEnvOrProp(new String[]{ENV_DB_USERNAME, "DB_USERNAME", "DB_USER"}, props, "db.username", DEFAULT_USERNAME);
            password = getEnvOrProp(new String[]{ENV_DB_PASSWORD, "DB_PASSWORD"}, props, "db.password", DEFAULT_PASSWORD);
            String driverClassName = getEnvOrProp(new String[]{"FITAURA_DB_DRIVER", "DB_DRIVER"}, props, "db.driver", DEFAULT_DRIVER);

            int maxPoolSize = parseInteger(getEnvOrProp(new String[]{ENV_DB_MAX_POOL_SIZE, "DB_MAX_POOL_SIZE"}, props, "db.pool.maximumPoolSize", "10"), 10);
            int minIdle = parseInteger(getEnvOrProp(new String[]{ENV_DB_MIN_IDLE, "DB_MIN_IDLE"}, props, "db.pool.minimumIdle", "2"), 2);
            long idleTimeout = parseLong(getEnvOrProp(new String[]{ENV_DB_IDLE_TIMEOUT, "DB_IDLE_TIMEOUT"}, props, "db.pool.idleTimeout", "30000"), 30000L);
            long connectionTimeout = parseLong(getEnvOrProp(new String[]{ENV_DB_CONNECTION_TIMEOUT, "DB_CONNECTION_TIMEOUT"}, props, "db.pool.connectionTimeout", "20000"), 20000L);

            logger.info("Initializing HikariCP pool for FitAura on database target: {}", sanitizeUrl(jdbcUrl));

            HikariConfig config = new HikariConfig();
            config.setPoolName("FitAuraHikariPool");
            config.setDriverClassName(driverClassName);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);
            config.setIdleTimeout(idleTimeout);
            config.setConnectionTimeout(connectionTimeout);
            config.setMaxLifetime(1800000L); // 30 minutes

            // Performance optimizations for MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            logger.info("FitAura HikariCP DataSource successfully initialized.");

        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage(), e);
            String message = "Could not initialize database connection pool: " + e.getMessage();
            if (password == null || password.trim().isEmpty()) {
                message += " (No database password was configured. Set DB_PASSWORD or FITAURA_DB_PASSWORD in environment or local.properties)";
            }
            throw new DatabaseException(message, e);
        }
    }

    /**
     * Obtains an active connection from the HikariCP pool.
     */
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            logger.error("Failed to acquire connection from pool: {}", e.getMessage());
            throw new DatabaseException("Unable to acquire database connection: " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether the connection pool can successfully obtain a connection
     * and execute a lightweight test query.
     */
    public static boolean isHealthy() {
        try {
            HikariDataSource ds = getDataSource();
            if (ds == null || ds.isClosed()) {
                return false;
            }
            try (Connection conn = ds.getConnection();
                 var stmt = conn.createStatement();
                 var rs = stmt.executeQuery("SELECT 1")) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (Exception e) {
            logger.warn("Database health check probe failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Safely closes the pool on application undeployment or shutdown.
     */
    public static void shutdown() {
        synchronized (lock) {
            if (dataSource != null && !dataSource.isClosed()) {
                logger.info("Shutting down FitAura HikariCP DataSource pool.");
                dataSource.close();
                dataSource = null;
            }
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = DatabaseConnectionPool.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception e) {
            logger.warn("Could not load application.properties, relying on environment or defaults: {}", e.getMessage());
        }

        // Support optional ignored local.properties for local development
        java.io.File localFile = new java.io.File("local.properties");
        if (localFile.exists() && localFile.canRead()) {
            try (InputStream in = new java.io.FileInputStream(localFile)) {
                properties.load(in);
                logger.info("Loaded local development configurations from local.properties");
            } catch (Exception e) {
                logger.warn("Could not load local.properties: {}", e.getMessage());
            }
        }

        return properties;
    }

    /**
     * Resolves property value, supporting Spring/standard ${ENV_VAR:defaultValue} placeholders.
     */
    private static String resolvePlaceholders(String val) {
        if (val == null) {
            return null;
        }
        String trimmed = val.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            String inner = trimmed.substring(2, trimmed.length() - 1);
            int colonIdx = inner.indexOf(':');
            String envKey;
            String defaultVal = "";
            if (colonIdx >= 0) {
                envKey = inner.substring(0, colonIdx).trim();
                defaultVal = inner.substring(colonIdx + 1).trim();
            } else {
                envKey = inner.trim();
            }
            String envVal = System.getenv(envKey);
            if (envVal != null && !envVal.trim().isEmpty()) {
                return envVal.trim();
            }
            return defaultVal;
        }
        return trimmed;
    }

    private static String getEnvOrProp(String[] envKeys, Properties props, String propKey, String defaultValue) {
        if (envKeys != null) {
            for (String envKey : envKeys) {
                String envVal = System.getenv(envKey);
                if (envVal != null && !envVal.trim().isEmpty()) {
                    return envVal.trim();
                }
            }
        }
        String propVal = props.getProperty(propKey);
        if (propVal != null) {
            String resolved = resolvePlaceholders(propVal);
            if (resolved != null && (!resolved.isEmpty() || "db.password".equals(propKey))) {
                return resolved;
            }
        }
        if (envKeys != null) {
            for (String envKey : envKeys) {
                String val = props.getProperty(envKey);
                if (val != null && !val.trim().isEmpty()) {
                    return val.trim();
                }
            }
        }
        return defaultValue;
    }

    private static String getEnvOrProp(String envKey, Properties props, String propKey, String defaultValue) {
        return getEnvOrProp(new String[]{envKey}, props, propKey, defaultValue);
    }

    private static int parseInteger(String val, int fallback) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static long parseLong(String val, long fallback) {
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String sanitizeUrl(String url) {
        if (url == null) return "";
        return url.replaceAll("password=[^;&]*", "password=***");
    }
}
