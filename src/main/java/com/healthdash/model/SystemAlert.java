package com.healthdash.model;

import java.sql.Timestamp;

public class SystemAlert {
    private long id;
    private Timestamp timestamp;
    private String metricType;
    private double threshold;
    private double actualValue;
    private String message;

    public SystemAlert() {}

    public SystemAlert(String metricType, double threshold, double actualValue, String message) {
        this.metricType = metricType;
        this.threshold = threshold;
        this.actualValue = actualValue;
        this.message = message;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    
    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
