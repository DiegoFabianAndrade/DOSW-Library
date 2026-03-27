# DOSW-Library

Proyecto de biblioteca. Libros, usuarios y prestamos guardados en postgres.

## Requisitos

- Java 21
- Maven
- Postgres instalado
- Docker para las pruebas funcionales (algunas fallan si no esta)

## Como correrlo

Crear la base en postgres:

```sql
CREATE DATABASE dosw_library;
CREATE USER dosw WITH PASSWORD 'dosw';
GRANT ALL PRIVILEGES ON DATABASE dosw_library TO dosw;
```

En la carpeta del proyecto:

```bash
./mvnw spring-boot:run
```

URL: `http://localhost:8080`

Si el usuario o contraseña de postgres es distinto, cambiar en `application.yaml`

## Swagger

`http://localhost:8080/swagger-ui.html` para ver y probar los endpoints

## Seguridad

La API usa JWT y no guarda sesion en servidor.

Login: `POST /auth/login` con body:

```json
{
  "username": "admin",
  "password": "Admin123!"
}
```

Ese usuario admin se crea automaticamente al iniciar la app (si no existe). Se puede cambiar con variables:

- `BOOTSTRAP_LIBRARIAN_USERNAME`
- `BOOTSTRAP_LIBRARIAN_PASSWORD`
- `BOOTSTRAP_LIBRARIAN_NAME`

El token se envia asi en cada request protegida:

`Authorization: Bearer <token>`

Roles:

- `LIBRARIAN`: gestionar libros, usuarios y prestamos
- `USER`: consultar libros y operar solo sus propios prestamos

Si falta token o es invalido retorna `401`.
Si el rol no alcanza o intenta ver prestamos de otro usuario retorna `403`.

## Endpoints

Libros: POST agregar, GET listar o por id, PATCH disponibilidad. Ruta /api/books

Usuarios: POST registrar, GET listar o por id. Ruta /api/users (solo LIBRARIAN)

Prestamos: POST crear (userId y bookId en el body), GET listar o por id, POST /return para devolver. Ruta /api/loans

## Lombok

Genera getters y setters automaticamente.

## Errores

GlobalExceptionHandler devuelve un json con el mensaje cuando hay error. Documentado en Swagger.

## Base de datos

Postgres con tablas users, books, loans. Config en application.yaml. Repositorios en persistence.

## HTTPS (SSL/TLS)

Hay soporte para HTTPS configurable por variables:

- `SSL_ENABLED=true`
- `SSL_KEY_STORE`
- `SSL_KEY_STORE_PASSWORD`
- `SSL_KEY_STORE_TYPE` (por defecto `PKCS12`)
- `SSL_KEY_ALIAS`

## Pruebas

```bash
./mvnw clean verify
```

Cobertura en target/site/jacoco/index.html

PMD en target/pmd.xml, no hace fallar el build.

## SonarCloud

```bash
./mvnw verify sonar:sonar -Dsonar.login=TU_TOKEN
```

## Carpetas

core: model, service, validator, util, exception

controller: rest y dto

persistence: entidades y repositorios

config: swagger
