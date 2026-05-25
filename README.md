# Invoice Engine

A distributed invoice processing system built with Java + Spring Boot.

## Tech Stack
- Java 21 + Spring Boot 3.5.14
- PostgreSQL (Docker)
- Apache Kafka (Docker)
- Spring Security

## Architecture
REST API → Kafka Message Queue → Background Worker → PDF Generation + Email Notification

## Running Locally
1. Start Docker containers: `docker compose up -d`
2. Run the app: `.\mvnw spring-boot:run`
3. API available at: `http://localhost:8080`
