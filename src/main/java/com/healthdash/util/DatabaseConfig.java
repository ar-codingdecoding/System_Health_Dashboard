package com.healthdash.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        try {
            // Force load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            // In a real app, these should come from environment variables or a properties
            // file.
            config.setJdbcUrl("jdbc:mysql://localhost:3306/health_dash?useSSL=false&serverTimezone=UTC");
            config.setUsername("root");
            config.setPassword("yourpassword"); // Default to empty for local testing, change as needed.

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);

            dataSource = new HikariDataSource(config);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL Driver: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized. Driver may be missing or config is wrong.");
        }
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
