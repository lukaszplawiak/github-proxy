# GitHub Proxy

[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-2563eb)](LICENSE)

A REST API proxy that lists non-fork GitHub repositories for a given user, including branch names and last commit SHAs. Built with Java 25 and Spring Boot 4 using declarative HTTP clients and integration-tested with WireMock.

[Overview](#overview) | [Tech Stack](#tech-stack) | [Running Locally](#running-locally) | [API](#api) | [Running Tests](#running-tests) | [Project Structure](#project-structure)

---

## Overview

The application acts as a proxy between the API consumer and the GitHub REST API v3. For a given GitHub username it returns all repositories that are not forks, with branch names and last commit SHAs for each repository.

### What the application covers

- **Repository listing** — fetches all non-fork repositories for a given GitHub user via `?type=sources`
- **Branch details** — for each repository fetches branch names and last commit SHAs
- **Error handling** — returns a structured `404` response when the GitHub user does not exist
- **Declarative HTTP client** — `@HttpExchange` interface backed by `RestClient` with no boilerplate
- **Integration tested** — full end-to-end tests with WireMock emulating the GitHub API

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0 |
| HTTP Client | RestClient + @HttpExchange |
| Build | Gradle with Kotlin DSL |
| Testing | JUnit 5 + WireMock + RestTestClient |
| Backing API | GitHub REST API v3 |

---

## Running Locally

### Prerequisites

- Java 25
- Gradle (or use the included `./gradlew` wrapper)

### Step 1 — Clone the repository

```bash
git clone https://github.com/lukaszplawiak/github-proxy.git
cd github-proxy
```

### Step 2 — Build the project

```bash
./gradlew build
```

### Step 3 — Run the application

```bash
./gradlew bootRun
```

The application starts on `http://localhost:8080`.

> **Note**: The application uses the public GitHub API without authentication.
> The unauthenticated rate limit is 60 requests per hour per IP address.

---

## API

### Get repositories for a user

```
GET /repositories/{username}
```

Returns all non-fork repositories for the given GitHub username, including branch names and last commit SHAs.

**Path parameters**

| Parameter | Type | Description |
|---|---|---|
| `username` | string | GitHub username |

**Success response** `200 OK`

```json
[
    {
        "repositoryName": "github-proxy",
        "ownerLogin": "lukaszplawiak",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "a1b2c3d4e5f6..."
            }
        ]
    }
]
```

**Error response** `404 Not Found`

Returned when the GitHub user does not exist.

```json
{
    "status": 404,
    "message": "User 'nonexistent' not found"
}
```

---

## Running Tests

```bash
./gradlew test
```

### Test coverage

| Test class | What it covers |
|---|---|
| `GithubControllerIntegrationTest` | Full end-to-end tests with WireMock emulating GitHub API |

| Test case | Description |
|---|---|
| `shouldReturnRepositoriesForExistingUser` | Happy path — returns repositories with branches |
| `shouldReturn404ForNonExistingUser` | Returns structured 404 when user does not exist |
| `shouldReturnEmptyListWhenAllRepositoriesAreForks` | Returns empty list when user has no non-fork repositories |

---

## Project Structure

```
github-proxy/
├── src/
│   ├── main/
│   │   ├── java/com/atipera/github_proxy/
│   │   │   ├── GithubProxyApplication.java     # Application entry point
│   │   │   ├── GithubController.java           # REST controller – GET /repositories/{username}
│   │   │   ├── GithubService.java              # Business logic – orchestrates client calls and maps models
│   │   │   ├── GithubClient.java               # @HttpExchange declarative HTTP client interface
│   │   │   ├── GithubClientConfig.java         # RestClient + HttpServiceProxyFactory configuration
│   │   │   ├── GithubRepository.java           # Internal model – GitHub API repository response
│   │   │   ├── GithubOwner.java                # Internal model – GitHub API owner response
│   │   │   ├── GithubBranch.java               # Internal model – GitHub API branch response
│   │   │   ├── GithubCommit.java               # Internal model – GitHub API commit response
│   │   │   ├── RepositoryDto.java              # Public DTO – repository response for API consumer
│   │   │   ├── BranchDto.java                  # Public DTO – branch response for API consumer
│   │   │   ├── ErrorResponse.java              # Public DTO – error response for API consumer
│   │   │   ├── UserNotFoundException.java      # Domain exception – thrown when GitHub user not found
│   │   │   └── GlobalExceptionHandler.java     # @RestControllerAdvice – maps exceptions to HTTP responses
│   │   └── resources/
│   │       └── application.yml                 # Application configuration
│   └── test/
│       ├── java/com/atipera/github_proxy/
│       │   ├── GithubControllerIntegrationTest.java  # Integration tests with WireMock
│       │   └── GithubProxyApplicationTests.java      # Spring context load test
│       └── resources/
│           └── application.yml                 # Test configuration – WireMock base URL
├── build.gradle.kts                            # Gradle build configuration (Kotlin DSL)
└── README.md
```

---

## Author

Built by Łukasz Pławiak as a recruitment task demonstrating Java 25 and Spring Boot 4 backend development.