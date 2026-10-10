# People

People is a Quarkus-based backend application for managing employees, skills, projects, allocations, and authentication. The project exposes a REST API and uses PostgreSQL persistence with Hibernate ORM and JWT-based security.

## Project overview

- Framework: Quarkus 3.39.3
- Language: Java
- Java version: 21
- Build tool: Gradle
- Persistence: Hibernate ORM + Panache, PostgreSQL
- API layer: REST endpoints with Jackson
- Security: SmallRye JWT and Elytron security
- Testing: JUnit, JUnit + REST Assured, H2 for tests

## Architecture and features

The application includes REST resources for:

- Employees
- Skills
- Projects
- Employee-to-skill mappings
- Allocations
- Authentication and login flow

The backend follows a typical Quarkus layered design with:

- Controller resources under `src/main/java/org/acme/controller`
- Service layer for business logic
- Repository layer for persistence access
- DTOs and exception handling for API responses

REST resources delegate request handling to services; services coordinate business
operations through repositories. JPA entities under `model` contain mappings only,
while Panache is used through the repository layer rather than the active-record pattern.

## Kafka messaging

Administrators can submit project JSON to `/api/kafka/projects`. The endpoint validates
the request and publishes it to Kafka topic `project_topic`; the consumer validates the
message and creates or updates the project. If `id` matches an existing project, that
project is updated. If `id` is omitted or does not match an existing project, a new
project is created. The broker bootstrap address is configured as `localhost:9092` in
`src/main/resources/application.properties`.

Project names may contain letters and spaces. Descriptions may contain letters, digits,
punctuation, and spaces. Status must be `PLANNING`, `ACTIVE`, `COMPLETED`, or `CANCELLED`.
Send a request with an administrator JWT:

```bash
curl -X POST http://localhost:8080/api/kafka/projects \
  -H "Authorization: Bearer <admin-jwt>" \
  -H "Content-Type: application/json" \
  --data '{"name":"Project Alpha","description":"A new project for 2026.","status":"PLANNING"}'
```

The endpoint returns `202 Accepted` when Kafka acknowledges the message. The consumer
listens on the same topic using consumer group `people-project-consumer`. The topic must
exist unless Kafka topic auto-creation is enabled.

## Java and GraalVM version

This project is configured for Java 21 in `build.gradle`:

- `sourceCompatibility = JavaVersion.VERSION_21`
- `targetCompatibility = JavaVersion.VERSION_21`

The repository does not explicitly pin a GraalVM version in the Gradle config. For native-image builds, use a GraalVM distribution compatible with Java 21, typically GraalVM for JDK 21. This matches the project’s Java target and Quarkus 3.39.3 native build compatibility.

## Prerequisites

- JDK 21
- GraalVM for JDK 21 if you plan to build a native executable
- PostgreSQL running locally or reachable from the app configuration
- Gradle wrapper included in the repo

## Running the application in dev mode

```bash
./gradlew quarkusDev
```

Quarkus Dev UI is available at:

- http://localhost:8080/q/dev/

## CORS

The API allows requests from the local Angular development origin `http://localhost:4200`.
For a deployed frontend, set `CORS_ORIGINS` to its exact origin or a comma-separated list of
exact origins (for example, `https://people.example.com`). Avoid wildcard origins for this
authenticated API. CORS allows the `GET`, `POST`, `PUT`, and `DELETE` methods and the
`Accept`, `Authorization`, and `Content-Type` headers.

## Building and running the application

Build the project:

```bash
./gradlew build
```

This generates the runnable application under `build/quarkus-app/`.

Run it with:

```bash
java -jar build/quarkus-app/quarkus-run.jar
```

## Native executable build

Create a native executable with:

```bash
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
```

If GraalVM is not installed locally, you can use a containerized native build:

```bash
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false -Dquarkus.native.container-build=true
```

The generated native runner will be placed in `build/` and can be started with:

```bash
./build/people-1.0.0-SNAPSHOT-runner
```

## Useful references

- Quarkus: https://quarkus.io/
- Quarkus REST: https://quarkus.io/guides/rest
- Hibernate ORM with Panache: https://quarkus.io/guides/hibernate-orm-panache
- PostgreSQL datasource guide: https://quarkus.io/guides/datasource
- Gradle tooling guide: https://quarkus.io/guides/gradle-tooling

## Notes

This project is a backend service and is primarily intended to be run as a local Quarkus application or packaged native binary. Configuration values, such as datasource and security settings, are expected to be supplied through Quarkus configuration files and environment variables.
