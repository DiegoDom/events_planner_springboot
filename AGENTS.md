# AGENTS.md

## Project

Single-module Spring Boot 4.1.2 / Java 21 Maven app (`artifactId: api`). Root package is `com.ddominguez.events.planner.api`. It is an early-stage events REST API exposed under `/api/v1/events`: `GET /api/v1/events` (list), `GET /api/v1/events/{id}`, `POST /api/v1/events` (create), `PUT /api/v1/events/{id}` (partial update) and `DELETE /api/v1/events/{id}`. Layers present: `domain` (`Event`), `repository` (`EventRepository`), `service` (`IEventService`/`EventService`), `controller` (`EventController`), `dto` (`EventRequestDto`/`EventUpdateRequestDto`/`EventResponseDto`), `mapper` (`EventMapper`, MapStruct) and `exception` (`GlobalExceptionHandler`, `ResourceNotFoundException`, `NoFieldsToUpdateException`). There are no migrations yet.

## Commands

Run from the repo root; use the Maven wrapper (Maven 3.10.0 is pinned in `.mvn/wrapper/maven-wrapper.properties`):

- `./mvnw test` — run Checkstyle, apply Spotless formatting, compile, and all tests
- `./mvnw -Dtest=ApiApplicationTests#contextLoads test` — run one test
- `./mvnw spring-boot:run` — start the app
- `./mvnw package` — build the jar in `target/`

Checkstyle and Spotless are bound to the `validate` phase, so `test`, `package`, and `spring-boot:run` all run them. Spotless is bound to `apply`, so builds format Java sources instead of failing; no manual `./mvnw spotless:apply` is needed before committing.

## Code quality

- Checkstyle rules live in `config/checkstyle/checkstyle.xml`; run `./mvnw checkstyle:check` directly.
- Spotless uses google-java-format 1.28.0 and is bound to `apply` in `validate`, so builds rewrite badly formatted sources instead of failing. Run `./mvnw spotless:check` to verify formatting without changing files (use it in CI); run `./mvnw spotless:apply` to format manually. Keep 1.28.0: 1.37.0 failed with this Spotless/Java 21 setup.
- Spotless owns formatting. Add semantic rules to Checkstyle, not whitespace/indentation rules that would fight google-java-format.
- `.vscode/settings.json` points the Checkstyle for Java extension at the same ruleset and configures the `JoseVSeb.google-java-format-for-vs-code` extension as the Java formatter with format-on-save, pinned to google-java-format 1.28.0 to match Spotless. `.vscode/extensions.json` lists the recommended Java/Spring/Checkstyle/formatter extensions. Only those two `.vscode` files are tracked.
- There is no CI configuration.

## Database

No datasource is configured, so the app and tests use an in-memory H2 database (`jdbc:h2:mem:<uuid>`, user `SA`) that is recreated on every start. The PostgreSQL driver is present but will not be used until `spring.datasource.*` is configured.

- `src/main/resources/application.properties` sets `spring.jpa.hibernate.ddl-auto=update` and `spring.jpa.show-sql=true`.
- The H2 console dependency is present but the console is disabled by default; enable it with `spring.h2.console.enabled=true` if needed.
- Schema is generated from JPA entities. Do not add Flyway/Liquibase assumptions without wiring them in.

## Spring Boot 4 specifics

This project uses Boot 4's split starter names. Do not "fix" them to Boot 3 names:

- `spring-boot-starter-webmvc` (not `spring-boot-starter-web`)
- `spring-boot-starter-webmvc-test` and `spring-boot-starter-data-jpa-test` (not `spring-boot-starter-test`)

Lombok and MapStruct annotation processing are explicitly configured in `maven-compiler-plugin` for both main and test compilation (with `lombok-mapstruct-binding` so Lombok getters/setters are visible to MapStruct); keep that configuration if the compiler plugin changes. `mapstruct` is a compile dependency and `mapstruct-processor` is an annotation processor path.

Boot 4 uses Jackson 3 (`tools.jackson.*`). Exception handlers and custom deserializers must import `tools.jackson.databind.*` / `tools.jackson.core.*`, not `com.fasterxml.jackson.databind.*`; the Jackson 2 exception classes are not on the classpath.

## Conventions

- Package by layer under `com.ddominguez.events.planner.api`: `domain`, `repository`, `service`, `controller`, `dto`, `mapper`, `exception`.
- `Event` uses Lombok `@Data`, is mapped to table `events`, and its date field is named `date` (not `data`); the generated column is `date`.
- The controller maps DTOs with the MapStruct `EventMapper` (`componentModel = "spring"`); entity fields and DTO fields share names so MapStruct maps them automatically — keep names in sync when renaming a field, and do a clean build (`./mvnw clean`) after renaming so `EventMapperImpl` is regenerated.
- `PUT /api/v1/events/{id}` is a partial update: `EventUpdateRequestDto` omits `@NotNull`/`@NotBlank` so omitted fields are `null`, and `EventMapper.updateEventFromDto` uses `NullValuePropertyMappingStrategy.IGNORE` so those fields keep their current value. A body with no fields returns 400 (`NoFieldsToUpdateException`).
- Validation and deserialization errors return `{status, error, message, errors}` with 400; `ResourceNotFoundException` returns 404 in the same shape. `GlobalExceptionHandler` also handles `HttpMessageNotReadableException`, so malformed payloads (including non-ISO dates) get a field-level message instead of Spring's default error body.
- Dates are `LocalDate`, so JSON uses ISO-8601 (`"2026-10-31"`). Formats like `31/10/2026` are rejected with a structured 400 in `errors.date`; there is no custom date format configured.
- Tests use JUnit 5 with `@SpringBootTest` + `@AutoConfigureMockMvc` (`org.springframework.boot.webmvc.test.autoconfigure`) and the H2 database; `EventControllerTest` covers validation, partial updates, 404 and invalid path variables.
