<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Health Dashboard</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="logo">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
                <span>HealthDash</span>
            </div>
            <nav class="nav-menu">
                <a href="#" class="nav-item active">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
                    Dashboard
                </a>
                <a href="#" class="nav-item">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>
                    Analytics
                </a>
                <a href="#" class="nav-item">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                    Alerts <span class="badge" id="alert-badge">0</span>
                </a>
            </nav>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="header">
                <div class="header-title">
                    <h1>System Overview</h1>
                    <p class="subtitle">Real-time performance metrics</p>
                </div>
                <div class="status-indicator">
                    <span class="pulse"></span>
                    <span id="system-status-text">System Normal</span>
                </div>
            </header>

            <!-- KPI Cards -->
            <section class="kpi-grid">
                <div class="kpi-card glass">
                    <div class="kpi-header">
                        <h3>CPU Usage</h3>
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="4" width="16" height="16" rx="2" ry="2"></rect><rect x="9" y="9" width="6" height="6"></rect><line x1="9" y1="1" x2="9" y2="4"></line><line x1="15" y1="1" x2="15" y2="4"></line><line x1="9" y1="20" x2="9" y2="23"></line><line x1="15" y1="20" x2="15" y2="23"></line><line x1="20" y1="9" x2="23" y2="9"></line><line x1="20" y1="14" x2="23" y2="14"></line><line x1="1" y1="9" x2="4" y2="9"></line><line x1="1" y1="14" x2="4" y2="14"></line></svg>
                    </div>
                    <div class="kpi-value">
                        <span id="cpu-value">0</span><span class="unit">%</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar" id="cpu-progress" style="width: 0%; background: var(--primary);"></div>
                    </div>
                </div>

                <div class="kpi-card glass">
                    <div class="kpi-header">
                        <h3>Memory Usage</h3>
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="12" x2="2" y2="12"></line><path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"></path><line x1="6" y1="16" x2="6.01" y2="16"></line><line x1="10" y1="16" x2="10.01" y2="16"></line></svg>
                    </div>
                    <div class="kpi-value">
                        <span id="memory-value">0</span><span class="unit">%</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar" id="memory-progress" style="width: 0%; background: var(--success);"></div>
                    </div>
                </div>

                <div class="kpi-card glass">
                    <div class="kpi-header">
                        <h3>Disk Usage</h3>
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#8b5cf6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><ellipse cx="12" cy="5" rx="9" ry="3"></ellipse><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path></svg>
                    </div>
                    <div class="kpi-value">
                        <span id="disk-value">0</span><span class="unit">%</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar" id="disk-progress" style="width: 0%; background: var(--purple);"></div>
                    </div>
                </div>
            </section>

            <!-- Charts Section -->
            <section class="charts-grid">
                <div class="chart-container glass">
                    <div class="chart-header">
                        <h3>Resource Usage Trend</h3>
                        <div class="chart-legend">
                            <span class="legend-item"><span class="dot" style="background: var(--primary);"></span> CPU</span>
                            <span class="legend-item"><span class="dot" style="background: var(--success);"></span> Memory</span>
                        </div>
                    </div>
                    <div class="canvas-wrapper">
                        <canvas id="trendChart"></canvas>
                    </div>
                </div>

                <div class="alerts-container glass">
                    <div class="chart-header">
                        <h3>Recent Alerts</h3>
                        <button id="clear-alerts" class="btn-icon" title="Clear Alerts">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                        </button>
                    </div>
                    <div class="alerts-list" id="alerts-list">
                        <!-- Alerts will be injected here via JS -->
                        <div class="empty-state">No active alerts. System is healthy.</div>
                    </div>
                </div>
            </section>
        </main>
    </div>

    <!-- Application Script -->
    <script src="js/dashboard.js"></script>
</body>
</html>
