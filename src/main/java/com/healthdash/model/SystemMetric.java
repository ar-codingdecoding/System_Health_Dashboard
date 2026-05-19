package com.healthdash.model;

import java.sql.Timestamp;

public class SystemMetric {
    private long id;
    private Timestamp timestamp;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;

    public SystemMetric() {}

    public SystemMetric(double cpuUsage, double memoryUsage, double diskUsage) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
}
