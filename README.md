# ♠️ Blackjack API - Reactive Spring WebFlux

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.8-green)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)]()
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)]()
[![Railway](https://img.shields.io/badge/Deploy-Railway-purple)](https://blackjack.up.railway.app/webjars/swagger-ui/index.html#/)

A fully reactive REST API for playing Blackjack, built with **Spring Boot WebFlux** and following **Hexagonal Architecture** principles. This project manages game state using **MongoDB** and player statistics using **MySQL (R2DBC)**.

---

## 📋 Project Status & Compliance

### 🎯 Version 1.0.0 (Assignment Submission)
**Version 1.0.0 of this repository fully satisfies all requirements specified in the "Tasca S5.01" assignment.**

It includes:
* Reactive API with Spring WebFlux.
* Hybrid persistence: **MongoDB** (Games) and **MySQL** (Players).
* Global Exception Handling.
* Unit Testing (Controllers & Use Cases).
* Swagger/OpenAPI Documentation.
* Dockerization and Deployment scripts.

### 🚀 Extended Functionality (Current Version: 1.1.0)
The current version extends beyond the initial requirements with the following **additional features**:
1.  **Multi-Deck Support**: Logic to play with 1 to 8 decks (configurable upon game creation).
2.  **Delete Player**: Endpoint to remove a player and cascade delete their game history.
3.  **Advanced Queries**:
    * `GET /game`: Retrieve a paginated list of all games.
    * `GET /game/player/{playerId}`: Retrieve game history for a specific player.

---

## 🚀 Deployment & Demo

The application is deployed and running on **Railway**. You can interact with the live API via the Swagger UI:

> **🔗 Live Demo:** [https://blackjack.up.railway.app/webjars/swagger-ui/index.html#/](https://blackjack.up.railway.app/webjars/swagger-ui/index.html#/)

This environment is automatically deployed from the `main` branch using GitHub Actions.

---

## 💡 Implementation Curiosities & Patterns

This project goes beyond the basics by implementing several advanced patterns and tools to ensure code quality and maintainability:

### 🏭 Object Mother Pattern
To maintain clean and readable tests, the **Object Mother** pattern is used for test data generation. Instead of cluttering test classes with complex object instantiation, factories like `GameMother` and `DeckMother` provide pre-configured objects for different testing scenarios.
* *Location*: `src/test/java/.../domain/model/aggregate/mother/`

### 📦 Testcontainers
Integration tests are powered by **Testcontainers**, ensuring that tests run against real, disposable instances of **MySQL** and **MongoDB** (via Docker) rather than in-memory mocks. This guarantees that the persistence layer behaves exactly as it would in production.
* *Configuration*: `src/test/resources/.testcontainers.properties`

### 📄 Reactive Pagination
Handling large lists of games efficiently is achieved through **Reactive Pagination**. The endpoints `GET /game` and `GET /game/player/{id}` allow clients to fetch data in pages, optimizing memory usage and response times in a non-blocking way.
* *Implementation*: `PageResponse<T>` DTO and Use Case logic.

### 📝 Structured Logging
Comprehensive logging is implemented throughout the application flow. This aids in debugging the asynchronous nature of WebFlux and provides visibility into critical operations like game creation, card dealing, and error handling.

---

## 🧪 Testing & Coverage

The project maintains a high level of code quality through a comprehensive test suite using **JUnit 5**, **Mockito**, and **Testcontainers**.

### 📊 Coverage Statistics
* **Total Tests**: 180+ passing tests.
* **Scope**: Covers Domain Logic, Application Use Cases, and Infrastructure Adapters.
* **Types**:
    * ✅ **Unit Tests**: Isolated testing of business rules (e.g., `HandTest`, `DeckTest`) and Use Cases.
    * ✅ **Integration Tests**: End-to-end testing of Controllers with `WebTestClient` and real database instances via `Testcontainers`.

### Running Tests
To execute the full test suite and verify coverage locally:

```bash
./mvnw test
```

### Tested Layers

| Layer | Focus | Examples |
| :--- | :--- | :--- |
| **Domain** | Core logic validation (rules, scoring) | `HandTest`, `DeckTest`, `GameTest` |
| **Application** | Use Case orchestration & mocking | `CreateGameUseCaseTest`, `PlayGameUseCaseTest` |
| **Infrastructure** | Web Controllers & API contracts | `GameControllerTest`, `GlobalExceptionHandlerTest` |
| **Integration** | Full stack verification (DB + API) | `GameControllerIntegrationTest`, `PlayerControllerIntegrationTest` |

---

## 🛠 Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.5.8 (WebFlux)
* **Database**:
    * **MongoDB**: Stores `Game`, `Hand`, `Deck`, and `Turn` data (Reactive Mongo).
    * **MySQL**: Stores `Player` profiles and statistics (R2DBC).
* **Architecture**: Domain-Driven Design (DDD) / Hexagonal.
* **Containerization**: Docker & Docker Compose.
* **CI/CD**: GitHub Actions for automated testing and Docker Hub publishing.

---

## 🔌 API Endpoints

### Core Endpoints (v1.0.0 Requirements)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/game/new` | Create a new game. Accepts `playerName`. |
| `GET` | `/game/{id}` | Get details of a specific game. |
| `POST` | `/game/{id}/play` | Execute a move (`HIT` or `STAND`). |
| `DELETE` | `/game/{id}/delete` | Delete a specific game. |
| `GET` | `/ranking` | Get the player ranking by win rate. |
| `PUT` | `/player/{id}` | Update a player's name. |

### Extended Endpoints (Additional Features v.1.1.0)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/game/new` | *Updated*: Accepts `numberOfDecks` (1-8) in body. |
| `DELETE` | `/player/{id}` | Delete a player and all associated games. |
| `GET` | `/game` | Get a paginated list of all games. |
| `GET` | `/game/player/{id}` | Get paginated game history for a specific player. |

---

## 🐳 Running the Application

### Prerequisites
* Docker & Docker Compose

### Quick Start
You can run the full stack (API, MySQL, and MongoDB) using Docker Compose:

```bash
# Build and start services
docker-compose up -d --build
```

The API will be available at: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Environment Variables
The application uses the following default configuration (defined in `.env.example` and `docker-compose.yml`):
* `MONGODB_PORT`: 27017
* `MYSQL_PORT`: 3306
* `SERVER_PORT`: 8080
