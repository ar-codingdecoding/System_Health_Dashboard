# System Health Monitoring Dashboard

A real-time, Java-based web application designed to monitor and analyze system performance metrics. Built from scratch with Servlets, JDBC, MySQL, and the OSHI library, this application tracks CPU, memory, and disk usage, displaying them on a premium interactive dashboard.

## Features
- **Real-Time Monitoring**: Automatically polls physical hardware metrics (CPU, Memory, Disk) every 5 seconds.
- **Alert System**: Generates automated alerts if system resource usage exceeds predefined thresholds (e.g., >80% CPU).
- **Interactive UI**: A modern, dark-themed dashboard built with Glassmorphism CSS, utilizing Chart.js for smooth trend visualization.
- **Persistent Logging**: Stores all historical metrics and alert logs in a MySQL database for operational analysis.
- **Automated Lifecycle**: Background workers automatically start and stop with the web server lifecycle.

## Tech Stack
- **Backend**: Java 11+, Servlet API, JSP
- **Hardware Integration**: OSHI (Operating System and Hardware Information)
- **Database**: MySQL, JDBC, HikariCP (Connection Pooling)
- **Frontend**: HTML5, Vanilla CSS, Vanilla JavaScript, Chart.js
- **Build Tool**: Maven

## Architecture
The application follows a standard Java Web Architecture:
- `MetricsCollectorService` runs on a scheduled thread, gathering hardware data via OSHI.
- Data is evaluated against thresholds and persisted to MySQL via HikariCP.
- `MetricsApiServlet` exposes RESTful JSON endpoints.
- The frontend `dashboard.js` uses `fetch()` to update Chart.js instances asynchronously.

## Setup Instructions

### 1. Database Configuration
1. Ensure you have MySQL running locally.
2. Execute the `schema.sql` file provided in the root directory to create the `health_dash` database and the required tables.
3. Open `src/main/java/com/healthdash/util/DatabaseConfig.java` and ensure the database username and password match your local MySQL credentials.

### 2. Running in IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA.
2. Reload the Maven project to resolve dependencies (OSHI, HikariCP, Gson, etc.).
3. Add a new **Tomcat Server (Local)** run configuration.
4. Go to the **Deployment** tab and add the `health-dashboard:war exploded` artifact. Set the Application Context to `/`.
5. Click Run! The dashboard will open in your browser at `http://localhost:8080/`.

## License
This project is open-source and available under the MIT License.
