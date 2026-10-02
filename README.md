# crediya-microservice-auth

[![CI](https://github.com/0103juan/crediya-microservice-auth/actions/workflows/ci.yml/badge.svg)](https://github.com/0103juan/crediya-microservice-auth/actions/workflows/ci.yml)

Users, roles and login for **CrediYa**, a fictional lender. It is one of two reactive microservices with a hexagonal architecture; the other is [crediya-microservice-loan](https://github.com/0103juan/crediya-microservice-loan), which asks this one who a user is and tells clients about their loans through an SQS queue and a Lambda function.

Java 21, Spring Boot 3.5 with WebFlux, R2DBC on PostgreSQL, JWT, and a clean architecture laid out with Bancolombia's open-source [scaffold plugin](https://github.com/bancolombia/scaffold-clean-architecture). The API messages are in Spanish; this README is in English.

## What it does

| Endpoint | Who can call it | What it does |
|---|---|---|
| `POST /api/v1/login` | anyone | Checks the password and returns a JWT |
| `POST /api/v1/users` | `ROLE_ADMIN`, `ROLE_ASESOR` | Registers a user with a role |
| `GET /api/v1/users/{idNumber}` | any signed-in user | Finds a user by document number |
| `GET /api/v1/users/email/{email}` | any signed-in user | Finds a user by email |
| `POST /api/v1/users/emails` | any signed-in user | Finds several users in one call, so the loan service does not ask once per row |

- Passwords are stored as BCrypt hashes.
- The token is signed with HS256, carries the user's role and expires after an hour. The secret comes from the `SECRET_JWT_TOKEN` environment variable and is not in the repository.
- A request body is validated before it reaches a use case; the answer lists every field that failed and why.
- An email or a document number that already exists is a `409`, not a database error.
- The contract is in `applications/app-service/src/main/resources/static/openapi/swagger.yaml`, served at `/swagger-ui.html`.

## How it is built

```
domain/model          User, Role, the exceptions and the gateway interfaces. No framework imports.
domain/usecase        RegisterUser, Login, FindUser: plain classes over the gateways.
infrastructure/
  driven-adapters/r2dbc-postgresql   the UserRepository gateway on R2DBC
  entry-points/reactive-web          router functions, handlers, JWT filter, error handler
  helpers/request-validator          Jakarta validation as a reactive step
applications/app-service             wiring and configuration
```

Dependencies point inwards: a use case knows `UserRepository` and `PasswordEncryptor` as interfaces, and the adapters implement them. `ArchitectureTest` enforces part of that with ArchUnit: domain classes may not be named after tools or technologies, and use cases and beans may only have final fields.

Everything is non-blocking, from the router function to the database driver.

## Tests

```bash
./gradlew test
```

51 tests in 17 classes: use cases with `StepVerifier` and mocked gateways, handlers through `WebTestClient`, the repository adapter, the mapper, the error handler and the architecture rules. The same command runs **PIT mutation testing** and writes JaCoCo coverage, so the build also reports how many deliberately broken versions of the code the tests catch. CI runs it on every push.

## Run it

It needs Java 21 and a PostgreSQL database called `auth`.

```bash
docker run -d --name crediya-pg -e POSTGRES_PASSWORD=admin -p 5432:5432 postgres:16
docker exec crediya-pg psql -U postgres -c "CREATE DATABASE auth"

export SECRET_JWT_TOKEN="any-string-of-at-least-32-characters-here"
export APP_DB_INIT_ENABLED=true      # creates the tables and the seed data on start
./gradlew bootRun                    # http://localhost:8081

curl -X POST localhost:8081/api/v1/login -H "Content-Type: application/json" \
     -d '{"email":"admin@crediya.com","password":"admin123"}'
```

The database address can be changed with `ADAPTERS_R2DBC_HOST`, `ADAPTERS_R2DBC_PORT`, `ADAPTERS_R2DBC_USERNAME` and `ADAPTERS_R2DBC_PASSWORD`.

I checked this by hand on 1 October 2026, together with the loan service: the seeded administrator signs in and registers an adviser and a client (`201`), a repeated email is a `409`, an invalid body is a `400` with the reasons per field, a client who tries to register a user gets a `403`, and a request without a token gets a `401`.

## Limits

- The seed data creates an administrator with a known password. That is for a local database only.
- There are no integration tests against a real PostgreSQL in the build; the adapter tests mock the repository. The check against a real database was done by hand, as described above.
- One symmetric secret signs and verifies the tokens, and the loan service needs the same secret. There is no key rotation, no refresh token and no logout.
- Any signed-in user can look up any other user. That is what the loan service needs, and more than a client should have.

## Where it comes from

The lender is fictional and this has never run in production. It began as the practice project of a backend training programme at Pragma, which is where the user stories and the `co.com.pragma` package name come from; the work was done in branches named after those stories (`HU1`, `HU3`, `HU5`) and `main` has all of it. The design decisions, the code and the tests described here are my own.
