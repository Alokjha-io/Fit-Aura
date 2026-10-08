package com.fitaura.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FitAura Application Context Lifecycle Listener.
 * Executes on application startup and shutdown:
 * - Bootstraps administrator account if FITAURA_ADMIN_EMAIL & FITAURA_ADMIN_PASSWORD environment variables are set.
 * - Safely cleans up HikariCP database connection pool on context destruction.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("FitAura application context initialized. Checking for administrator bootstrap configuration...");
        try {
            AdminBootstrapService.initializeAdminAccount();
        } catch (Exception e) {
            logger.error("Error during server administrator bootstrap on startup: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("FitAura application context destroying. Shutting down database connection pool...");
        try {
            DatabaseConnectionPool.shutdown();
        } catch (Exception e) {
            logger.warn("Error shutting down database connection pool: {}", e.getMessage());
        }
    }
}
