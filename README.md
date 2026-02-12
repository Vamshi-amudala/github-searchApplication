# GitHub Repository Searcher

A Spring Boot application that allows users to search for GitHub repositories using the GitHub REST API.
Results are stored in a PostgreSQL database for caching and efficient retrieval.

## Features
- Search GitHub repositories by name, language, and sorting options.
- Cache results in a PostgreSQL database.
- Retrieve stored repositories with filtering and sorting.
- RESTful API design.

## Tech Stack
- Java 17
- Spring Boot 3.x
- PostgreSQL
- Maven
- Lombok

## Prerequisites
- Java 17 Development Kit
- Maven
- PostgreSQL running locally (default: `localhost:5432`, db: `github_searcher`, user: `postgres`, password: `password`)

## Setup & Run

1.  **Database Setup**
    Ensure PostgreSQL is running and create the database:
    ```sql
    CREATE DATABASE github_searcher;
    ```

2.- [x] Configuration
    - [x] Externalize secrets to environment variables <!-- id: 17 -->
    The application now uses environment variables for sensitive configuration. 
    You can set these in your IDE run configuration or as system environment variables.
    
    Refer to `.env.example` for the required variables:
    - `DB_URL`
    - `DB_USERNAME`
    - `DB_PASSWORD`

3.  **Run Application**
    ```bash
    # Example running with inline env vars (Linux/Mac/Git Bash)
    DB_URL=jdbc:postgresql://localhost:5432/github_searcher \
    DB_USERNAME=postgres \
    DB_PASSWORD=password \
    mvn spring-boot:run
    ```

## API Documentation

### 1. Search GitHub Repositories
Fetches from GitHub and saves to DB.

- **Endpoint**: `POST /api/github/search`
- **Body**:
    ```json
    {
      "query": "spring boot",
      "language": "Java",
      "sort": "stars"
    }
    ```
- **Response**:
    ```json
    {
      "message": "Repositories fetched and saved successfully",
      "repositories": [...]
    }
    ```

### 2. Retrieve Stored Repositories
Fetches from local DB.

- **Endpoint**: `GET /api/github/repositories`
- **Parameters**:
    - `language`: (Optional) Filter by language
    - `minStars`: (Optional) Minimum stars
    - `sort`: (Optional) `stars` (default), `forks`, `updated`
- **Example**:
    `GET /api/github/repositories?language=Java&minStars=100&sort=stars`

## Testing
Run unit and integration tests:
```bash
mvn test
```
