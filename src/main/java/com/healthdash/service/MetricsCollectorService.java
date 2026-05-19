package com.healthdash.service;

import com.healthdash.model.SystemAlert;
import com.healthdash.model.SystemMetric;
import com.healthdash.util.DatabaseConfig;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsCollectorService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hal = systemInfo.getHardware();
    private final OperatingSystem os = systemInfo.getOperatingSystem();
    
    // Thresholds
    private static final double CPU_THRESHOLD = 80.0;
    private static final double MEMORY_THRESHOLD = 85.0;
    private static final double DISK_THRESHOLD = 90.0;

    private long[] prevTicks = new long[CentralProcessor.TickType.values().length];
    
    public void start() {
        System.out.println("Starting MetricsCollectorService...");
        // Initialize CPU ticks
        prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
        
        // Schedule to run every 5 seconds
        scheduler.scheduleAtFixedRate(this::collectAndProcessMetrics, 0, 5, TimeUnit.SECONDS);
    }
    
    public void stop() {
        System.out.println("Stopping MetricsCollectorService...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    
    private void collectAndProcessMetrics() {
        try {
            // 1. Collect Metrics
            double cpuUsage = getCpuUsage();
            double memoryUsage = getMemoryUsage();
            double diskUsage = getDiskUsage();
            
            SystemMetric metric = new SystemMetric(cpuUsage, memoryUsage, diskUsage);
            
            // 2. Save Metrics to Database
            saveMetric(metric);
            
            // 3. Evaluate Thresholds and Generate Alerts
            evaluateAndAlert(metric);
            
        } catch (Exception e) {
            System.err.println("Error collecting metrics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private double getCpuUsage() {
        CentralProcessor processor = hal.getProcessor();
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();
        return cpuLoad;
    }
    
    private double getMemoryUsage() {
        GlobalMemory memory = hal.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;
        return ((double) used / total) * 100;
    }
    
    private double getDiskUsage() {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        long totalSpace = 0;
        long usableSpace = 0;
        
        for (OSFileStore fs : fileStores) {
            totalSpace += fs.getTotalSpace();
            usableSpace += fs.getUsableSpace();
        }
        
        if (totalSpace == 0) return 0;
        long usedSpace = totalSpace - usableSpace;
        return ((double) usedSpace / totalSpace) * 100;
    }
    
    private void saveMetric(SystemMetric metric) {
        String sql = "INSERT INTO system_metrics (cpu_usage, memory_usage, disk_usage) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, metric.getCpuUsage());
            pstmt.setDouble(2, metric.getMemoryUsage());
            pstmt.setDouble(3, metric.getDiskUsage());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Failed to save metric: " + e.getMessage());
        }
    }
    
    private void evaluateAndAlert(SystemMetric metric) {
        if (metric.getCpuUsage() > CPU_THRESHOLD) {
            saveAlert(new SystemAlert("CPU", CPU_THRESHOLD, metric.getCpuUsage(), 
                    String.format("High CPU usage detected: %.2f%%", metric.getCpuUsage())));
        }
        
        if (metric.getMemoryUsage() > MEMORY_THRESHOLD) {
            saveAlert(new SystemAlert("Memory", MEMORY_THRESHOLD, metric.getMemoryUsage(), 
                    String.format("High Memory usage detected: %.2f%%", metric.getMemoryUsage())));
        }
        
        if (metric.getDiskUsage() > DISK_THRESHOLD) {
            saveAlert(new SystemAlert("Disk", DISK_THRESHOLD, metric.getDiskUsage(), 
                    String.format("High Disk usage detected: %.2f%%", metric.getDiskUsage())));
        }
    }
    
    private void saveAlert(SystemAlert alert) {
        String sql = "INSERT INTO system_alerts (metric_type, threshold, actual_value, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, alert.getMetricType());
            pstmt.setDouble(2, alert.getThreshold());
            pstmt.setDouble(3, alert.getActualValue());
            pstmt.setString(4, alert.getMessage());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Failed to save alert: " + e.getMessage());
        }
    }
}
