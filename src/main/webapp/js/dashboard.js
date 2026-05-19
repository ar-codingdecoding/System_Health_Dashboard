document.addEventListener('DOMContentLoaded', () => {
    // Chart Initialization
    const ctx = document.getElementById('trendChart').getContext('2d');
    
    // Gradient for CPU
    const cpuGradient = ctx.createLinearGradient(0, 0, 0, 400);
    cpuGradient.addColorStop(0, 'rgba(59, 130, 246, 0.5)');
    cpuGradient.addColorStop(1, 'rgba(59, 130, 246, 0.0)');
    
    // Gradient for Memory
    const memGradient = ctx.createLinearGradient(0, 0, 0, 400);
    memGradient.addColorStop(0, 'rgba(16, 185, 129, 0.5)');
    memGradient.addColorStop(1, 'rgba(16, 185, 129, 0.0)');

    const trendChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [
                {
                    label: 'CPU Usage %',
                    data: [],
                    borderColor: '#3b82f6',
                    backgroundColor: cpuGradient,
                    borderWidth: 2,
                    pointRadius: 0,
                    pointHoverRadius: 4,
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Memory Usage %',
                    data: [],
                    borderColor: '#10b981',
                    backgroundColor: memGradient,
                    borderWidth: 2,
                    pointRadius: 0,
                    pointHoverRadius: 4,
                    fill: true,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    display: false // We use custom HTML legend
                },
                tooltip: {
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    titleColor: '#f8fafc',
                    bodyColor: '#f8fafc',
                    borderColor: 'rgba(255,255,255,0.1)',
                    borderWidth: 1,
                    padding: 10
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(255, 255, 255, 0.05)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#94a3b8',
                        maxTicksLimit: 6
                    }
                },
                y: {
                    min: 0,
                    max: 100,
                    grid: {
                        color: 'rgba(255, 255, 255, 0.05)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#94a3b8',
                        stepSize: 25,
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });

    // DOM Elements
    const elements = {
        cpuVal: document.getElementById('cpu-value'),
        cpuProg: document.getElementById('cpu-progress'),
        memVal: document.getElementById('memory-value'),
        memProg: document.getElementById('memory-progress'),
        diskVal: document.getElementById('disk-value'),
        diskProg: document.getElementById('disk-progress'),
        alertsList: document.getElementById('alerts-list'),
        alertBadge: document.getElementById('alert-badge'),
        sysStatus: document.getElementById('system-status-text')
    };

    // Formatter
    const formatTime = (dateStr) => {
        const d = new Date(dateStr);
        return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    };

    // Fetch and Update
    const fetchMetrics = async () => {
        try {
            // Adjust the URL if the app context path is different
            const response = await fetch('api/metrics');
            if (!response.ok) throw new Error('Network response was not ok');
            const data = await response.json();
            
            updateDashboard(data);
        } catch (error) {
            console.error('Error fetching metrics:', error);
            elements.sysStatus.textContent = 'Disconnected';
            elements.sysStatus.parentElement.style.color = 'var(--danger)';
            elements.sysStatus.previousElementSibling.style.backgroundColor = 'var(--danger)';
        }
    };

    const updateDashboard = (data) => {
        if (!data) return;

        // 1. Update KPIs (Latest)
        if (data.latest) {
            const cpu = data.latest.cpuUsage.toFixed(1);
            const mem = data.latest.memoryUsage.toFixed(1);
            const disk = data.latest.diskUsage.toFixed(1);

            elements.cpuVal.textContent = cpu;
            elements.cpuProg.style.width = `${cpu}%`;
            
            elements.memVal.textContent = mem;
            elements.memProg.style.width = `${mem}%`;
            
            elements.diskVal.textContent = disk;
            elements.diskProg.style.width = `${disk}%`;

            // Status logic
            if (cpu > 80 || mem > 85 || disk > 90) {
                elements.sysStatus.textContent = 'Warning';
                elements.sysStatus.parentElement.style.color = 'var(--warning)';
                elements.sysStatus.previousElementSibling.style.backgroundColor = 'var(--warning)';
            } else {
                elements.sysStatus.textContent = 'System Normal';
                elements.sysStatus.parentElement.style.color = 'var(--success)';
                elements.sysStatus.previousElementSibling.style.backgroundColor = 'var(--success)';
            }
        }

        // 2. Update Chart (History)
        if (data.history && data.history.length > 0) {
            const labels = [];
            const cpuData = [];
            const memData = [];

            data.history.forEach(item => {
                labels.push(formatTime(item.timestamp));
                cpuData.push(item.cpuUsage);
                memData.push(item.memoryUsage);
            });

            trendChart.data.labels = labels;
            trendChart.data.datasets[0].data = cpuData;
            trendChart.data.datasets[1].data = memData;
            trendChart.update();
        }

        // 3. Update Alerts
        if (data.alerts) {
            elements.alertBadge.textContent = data.alerts.length;
            
            if (data.alerts.length === 0) {
                elements.alertsList.innerHTML = '<div class="empty-state">No active alerts. System is healthy.</div>';
            } else {
                elements.alertsList.innerHTML = data.alerts.map(alert => `
                    <div class="alert-item">
                        <div class="alert-header">
                            <span style="font-weight: 600;">${alert.metricType} Alert</span>
                            <span>${formatTime(alert.timestamp)}</span>
                        </div>
                        <div class="alert-msg">${alert.message}</div>
                    </div>
                `).join('');
            }
        }
    };

    // Clear alerts listener (mock behavior for UI)
    document.getElementById('clear-alerts').addEventListener('click', () => {
        elements.alertsList.innerHTML = '<div class="empty-state">No active alerts. System is healthy.</div>';
        elements.alertBadge.textContent = '0';
    });

    // Start polling every 5 seconds
    fetchMetrics();
    setInterval(fetchMetrics, 5000);
});
