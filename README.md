# Spring Microservices Starter

[![CI](https://github.com/jainmohit0807/spring-microservices-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/jainmohit0807/spring-microservices-starter/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0-6DB33F?logo=spring)](https://spring.io/projects/spring-cloud)
[![Kafka](https://img.shields.io/badge/Apache_Kafka-7.7-231F20?logo=apachekafka)](https://kafka.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A **production-ready microservices template** with Spring Cloud 2023.0 and Java 21. Features service discovery (Eureka), centralized configuration, API gateway with rate limiting and circuit breakers, JWT-based authentication with RBAC, event-driven notifications via Kafka, and full Docker Compose orchestration.

> **Designed as a reusable template — fork, rename, and build your microservices platform.**

---

## Services

| Service | Port | Description |
|---|---|---|
| **eureka-server** | 8761 | Service discovery and registry |
| **config-server** | 8888 | Centralized configuration (native filesystem) |
| **api-gateway** | 8080 | Spring Cloud Gateway with rate limiting, circuit breakers, load balancing |
| **user-service** | 8081 | Registration, JWT auth, RBAC (USER/ADMIN/MODERATOR) |
| **notification-service** | 8082 | Kafka consumer for email/SMS notifications |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                           Client                                      │
└────────────────────────────┬─────────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────────┐
│                    API Gateway (:8080)                                 │
│  Rate Limiting (Redis) · Circuit Breaker (Resilience4j) · Routing     │
├──────────────────┬─────────────────────┬─────────────────────────────┤
│                  │                     │                               │
│    ┌─────────────▼──────┐  ┌──────────▼───────────┐                  │
│    │  user-service       │  │  notification-service  │                  │
│    │  (:8081)            │  │  (:8082)               │                  │
│    │  • Registration     │  │  • Email (SMTP)        │                  │
│    │  • JWT Auth + RBAC  │  │  • SMS (Twilio/mock)   │                  │
│    │  • Account lockout  │  │  • Kafka consumer      │                  │
│    └──────┬──────────────┘  └──────────┬─────────────┘                │
│           │                            │                               │
│           ├── Kafka ──────────────────►│                               │
│           │   (notification-events)    │                               │
│           ▼                            ▼                               │
│    ┌──────────────┐          ┌──────────────┐                         │
│    │  PostgreSQL   │          │  PostgreSQL   │                         │
│    │  (user_db)    │          │  (notif_db)   │                         │
│    └──────────────┘          └──────────────┘                         │
├──────────────────────────────────────────────────────────────────────┤
│  Eureka (:8761) — Discovery    Config Server (:8888) — Configuration  │
│  Redis — Rate Limiting         Kafka + Zookeeper — Event Bus          │
└──────────────────────────────────────────────────────────────────────┘
```

For detailed diagrams, see [docs/architecture.md](docs/architecture.md).

---

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)

### Run with Docker (2 commands)

```bash
git clone https://github.com/jainmohit0807/spring-microservices-starter.git
cd spring-microservices-starter
docker compose up -d
```

Services start in order: Eureka → Config → Infrastructure → Application services.

- **API Gateway:** `http://localhost:8080`
- **Eureka Dashboard:** `http://localhost:8761`
- **User Service Swagger:** `http://localhost:8081/api/swagger-ui.html`

### Run Locally (single service)

```bash
# Start infrastructure
docker compose up -d postgres redis kafka zookeeper eureka-server config-server

# Run user-service with local profile (H2, no Kafka/Eureka)
cd user-service && ../mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### IntelliJ IDEA

1. **Open** the root project folder (multi-module Maven).
2. **Maven** → **Reload All Maven Projects**.
3. **Settings → Build Tools → Maven** → **User settings**: Override → `.mvn/settings-public.xml`.
4. Run any module's `*Application.java` with VM option `-Dspring.profiles.active=local`.

---

## API Reference

All endpoints accessible via Gateway (`:8080`) or directly.

### Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/login` | Public | Login and receive JWT tokens |

### Users

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/users` | Public | Register a new user |
| GET | `/api/v1/users/{id}` | Bearer | Get user by ID |
| GET | `/api/v1/users` | Admin | List all users (paginated) |

### Example Flow

```bash
# 1. Register via Gateway
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"SecurePass123!","firstName":"John","lastName":"Doe"}'

# 2. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"SecurePass123!"}'

# 3. Access protected resource
curl http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer <accessToken>"
```

A [Postman collection](postman/spring-microservices-starter.postman_collection.json) is included.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 3.3 |
| Cloud | Spring Cloud 2023.0.4 (Eureka, Config, Gateway) |
| Security | Spring Security 6, JJWT 0.12 |
| Messaging | Apache Kafka (Confluent 7.7) |
| Database | PostgreSQL 16 |
| Cache | Redis 7 (Gateway rate limiting) |
| Resilience | Resilience4j (circuit breaker) |
| API Docs | SpringDoc OpenAPI 2.6 |
| Containerization | Docker multi-stage builds |
| CI/CD | GitHub Actions |
| Testing | JUnit 5, Mockito, MockMvc |

---

## Project Structure

```
spring-microservices-starter/
├── eureka-server/          # Netflix Eureka service registry
├── config-server/          # Centralized config (native filesystem)
│   └── configurations/     # Per-service YAML config files
├── api-gateway/            # Spring Cloud Gateway + rate limiting
├── user-service/           # User CRUD, JWT auth, RBAC
│   ├── controller/         # REST endpoints
│   ├── service/            # Business logic + Kafka publisher
│   ├── security/           # JWT provider, auth filter
│   ├── entity/             # JPA entities
│   └── exception/          # RFC 7807 error handling
├── notification-service/   # Kafka consumer → email/SMS
│   ├── listener/           # Kafka event listener
│   ├── service/            # Email and SMS services
│   └── entity/             # Notification log entity
├── docker-compose.yml      # Full stack orchestration
└── postman/                # API collection
```

---

## Testing

```bash
# All modules
./mvnw test

# Single module
./mvnw test -pl user-service

# Skip tests in build
./mvnw package -DskipTests
```

---

## Roadmap

- [ ] OAuth 2.0 / OpenID Connect integration
- [ ] Distributed tracing (Micrometer + Zipkin)
- [ ] Kubernetes Helm charts
- [ ] Saga pattern for distributed transactions
- [ ] Admin dashboard (React)
- [ ] gRPC inter-service communication option
- [ ] API versioning strategy

---

## Contributing

Contributions are welcome! Please open an issue first to discuss proposed changes.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## Author

**Mohit Jain** — Tech Lead | Backend Engineering
[GitHub](https://github.com/jainmohit0807) · [LinkedIn](https://linkedin.com/in/mohit-jain-264296115) · [Blog](https://jainmohit0807.hashnode.dev)
