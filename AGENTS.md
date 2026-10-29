# AGENTS.md

## Project

Single-module Spring Boot 4.1.2 / Java 21 Maven app (`artifactId: api`). Root package is `com.ddominguez.events.planner.api`. It is an early-stage events REST API: only `ApiApplication`, the `Event` entity, and `EventRepository` exist — there are no controllers, services, DTOs, migrations, or exception handlers yet.

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

Lombok annotation processing is explicitly configured in `maven-compiler-plugin` for both main and test compilation; keep that configuration if the compiler plugin changes.

## Conventions

- Package by layer under `com.ddominguez.events.planner.api` (e.g. `domain`, `repository`; add `service`/`controller`/`dto` as needed).
- `Event` uses Lombok `@Data`, is mapped to table `events`, and its date field is named `data` (not `date`); the generated column is `data`.
- Tests use JUnit 5 and Spring Boot test slices/starters from the Boot 4 packages above.
