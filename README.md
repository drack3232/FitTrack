# FitTrack Backend

A robust REST API backend for the FitTrack application, designed to help users track their daily nutrition (macros, calories) and physical activity. The service utilizes a stateless architecture with JWT-based authentication for secure data access.

## Tech Stack
* **Java 21**
* **Spring Boot 3** (Web, Security, Data JPA)
* **PostgreSQL**
* **Gradle**
* **JSON Web Tokens (JWT)**

## Getting Started

### Prerequisites
Ensure you have the following installed on your system:
* [Java 21](https://jdk.java.net/21/)
* [Git](https://git-scm.com/)
* [Docker](https://www.docker.com/) (recommended for isolated PostgreSQL setup)

### Running the Backend

**1. Clone the repository**
```bash
git clone https://github.com/your-username/fittrack.git
cd fittrack
```

**2. Set up the Database**
Ensure a PostgreSQL instance is running. If your project includes a `docker-compose.yml` for the database, start it with:
```bash
docker-compose up -d
```
*Note: Verify that the database connection parameters (URL, username, password) in `src/main/resources/application.properties` match your active PostgreSQL environment.*

**3. Build and Run**
Use the provided Gradle wrapper to launch the application without installing Gradle globally:

```bash
# On Linux/macOS
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

The server will start and listen for API requests at `http://localhost:8080`.
