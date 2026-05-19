package com.healthdash.listener;

import com.healthdash.service.MetricsCollectorService;
import com.healthdash.util.DatabaseConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private MetricsCollectorService metricsCollectorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("System Health Dashboard Application is starting...");
        
        // Ensure Database connection is valid
        try {
            DatabaseConfig.getConnection().close();
            System.out.println("Database connection pool initialized successfully.");
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to initialize database on startup.");
            e.printStackTrace();
        }

        // Start metrics collector
        metricsCollectorService = new MetricsCollectorService();
        metricsCollectorService.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("System Health Dashboard Application is shutting down...");
        
        if (metricsCollectorService != null) {
            metricsCollectorService.stop();
        }
        
        // Close database connection pool
        DatabaseConfig.closePool();
    }
}
