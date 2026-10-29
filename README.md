# Events Planner API

API REST para la gestión de eventos, construida con Spring Boot 4 y Java 21.

> **Estado:** proyecto en construcción. Actualmente existen la entidad `Event` y su repositorio; todavía no hay controladores, servicios ni endpoints públicos.

## Stack

- Java 21
- Spring Boot 4.1.2
  - Spring Web MVC
  - Spring Data JPA (Hibernate)
- Maven Wrapper 3.10.0
- H2 en memoria para desarrollo y pruebas
- PostgreSQL (driver incluido, pendiente de configurar)
- Lombok

## Requisitos

- JDK 21
- No necesitas instalar Maven: el proyecto incluye `./mvnw`

## Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

## Pruebas

```bash
./mvnw test
```

Para ejecutar una sola prueba:

```bash
./mvnw -Dtest=ApiApplicationTests#contextLoads test
```

## Calidad de código

El proyecto usa **Checkstyle** para reglas estáticas y **Spotless** con google-java-format para el formato. Ambos se ejecutan en la fase `validate`, así que `./mvnw test` y `./mvnw package` fallan si el código no cumple.

```bash
./mvnw checkstyle:check   # reglas de Checkstyle
./mvnw spotless:check     # verifica el formato
./mvnw spotless:apply     # aplica el formato automáticamente
```

- Reglas de Checkstyle: `config/checkstyle/checkstyle.xml`
- Configuración de Spotless: `pom.xml`

### VS Code

Al abrir el proyecto, VS Code recomienda las extensiones necesarias (Extension Pack for Java, Spring Boot y Checkstyle for Java). La configuración compartida está en `.vscode/settings.json` y apunta a la misma configuración de Checkstyle que usa Maven, por lo que los errores aparecen también en el editor.

El proyecto no aplica formato al guardar. Después de editar archivos Java, ejecuta:

```bash
./mvnw spotless:apply
```

## Base de datos

La aplicación usa **H2 en memoria** por defecto. No hay un datasource configurado en `src/main/resources/application.properties`, por lo que los datos se pierden al reiniciar. El driver de PostgreSQL está incluido, pero no se conecta a PostgreSQL hasta que configures `spring.datasource.*`.

Propiedades actuales:

```properties
spring.application.name=api_events_planner
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

El esquema se genera a partir de las entidades JPA. Para usar PostgreSQL, agrega la configuración del datasource, por ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/events_planner
spring.datasource.username=postgres
spring.datasource.password=postgres
```

No guardes credenciales reales en el repositorio; usa variables de entorno o un archivo de configuración local.

La consola de H2 está incluida pero deshabilitada. Actívala con `spring.h2.console.enabled=true` si la necesitas.

## Estructura

```text
src/main/java/com/ddominguez/events/planner/api/
├── ApiApplication.java
├── domain/
│   └── Event.java
└── repository/
    └── EventRepository.java
```

La entidad `Event` se mapea a la tabla `events` y contiene `id`, `name`, `data` (fecha) y `location`. El campo de fecha se llama `data`, no `date`.
