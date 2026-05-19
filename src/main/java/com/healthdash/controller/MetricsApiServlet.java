package com.healthdash.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.healthdash.model.SystemAlert;
import com.healthdash.model.SystemMetric;
import com.healthdash.util.DatabaseConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/metrics")
public class MetricsApiServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject responseJson = new JsonObject();
        
        try {
            // Get latest metric (1 point for the gauges)
            SystemMetric latestMetric = getLatestMetric();
            
            // Get history metrics (for the line charts, e.g., last 30 points)
            List<SystemMetric> history = getHistoryMetrics(30);
            
            // Get recent alerts (e.g., last 10)
            List<SystemAlert> alerts = getRecentAlerts(10);
            
            responseJson.add("latest", gson.toJsonTree(latestMetric));
            responseJson.add("history", gson.toJsonTree(history));
            responseJson.add("alerts", gson.toJsonTree(alerts));
            
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseJson.addProperty("error", "Database error: " + e.getMessage());
        }

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(responseJson));
            out.flush();
        }
    }

    private SystemMetric getLatestMetric() throws SQLException {
        String sql = "SELECT * FROM system_metrics ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                SystemMetric metric = new SystemMetric();
                metric.setId(rs.getLong("id"));
                metric.setTimestamp(rs.getTimestamp("timestamp"));
                metric.setCpuUsage(rs.getDouble("cpu_usage"));
                metric.setMemoryUsage(rs.getDouble("memory_usage"));
                metric.setDiskUsage(rs.getDouble("disk_usage"));
                return metric;
            }
        }
        return null;
    }

    private List<SystemMetric> getHistoryMetrics(int limit) throws SQLException {
        List<SystemMetric> metrics = new ArrayList<>();
        // Note: Getting descending to get the latest, then we would typically reverse them in UI
        String sql = "SELECT * FROM (SELECT * FROM system_metrics ORDER BY timestamp DESC LIMIT ?) sub ORDER BY timestamp ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SystemMetric metric = new SystemMetric();
                    metric.setId(rs.getLong("id"));
                    metric.setTimestamp(rs.getTimestamp("timestamp"));
                    metric.setCpuUsage(rs.getDouble("cpu_usage"));
                    metric.setMemoryUsage(rs.getDouble("memory_usage"));
                    metric.setDiskUsage(rs.getDouble("disk_usage"));
                    metrics.add(metric);
                }
            }
        }
        return metrics;
    }

    private List<SystemAlert> getRecentAlerts(int limit) throws SQLException {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM system_alerts ORDER BY timestamp DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SystemAlert alert = new SystemAlert();
                    alert.setId(rs.getLong("id"));
                    alert.setTimestamp(rs.getTimestamp("timestamp"));
                    alert.setMetricType(rs.getString("metric_type"));
                    alert.setThreshold(rs.getDouble("threshold"));
                    alert.setActualValue(rs.getDouble("actual_value"));
                    alert.setMessage(rs.getString("message"));
                    alerts.add(alert);
                }
            }
        }
        return alerts;
    }
}
