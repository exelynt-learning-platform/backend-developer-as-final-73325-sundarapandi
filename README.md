# Resource Booking System

A secure RESTful booking API built with Java 17, Spring Boot, Spring Security, JWT, JPA/Hibernate and MySQL.

## Features

- JWT login at `POST /api/auth/login`
- BCrypt password hashing
- Stateless JWT authentication
- ADMIN and USER roles
- ADMIN full CRUD for resources and reservations
- USER read-only resource access
- USER creates reservations and sees only their own reservations
- Reservation ownership is derived from the authenticated JWT identity
- Reservation statuses: `PENDING`, `CONFIRMED`, `CANCELLED`
- Decimal prices using `BigDecimal`
- Reservation filtering by status/minPrice/maxPrice
- Pagination with `page` and `size`
- Optional sorting with `sort=field,direction`
- Reservation time validation and overlap prevention
- Global validation/error handling
- Swagger/OpenAPI documentation
- Seed ADMIN and USER accounts
- Unit/security tests

## Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+
- IntelliJ IDEA / VS Code / Eclipse

## Database setup

```sql
CREATE DATABASE resource_booking;
```

Set these environment variables. Do not commit database passwords or JWT signing secrets to source control.

Windows PowerShell example:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="replace-with-a-long-random-secret-at-least-32-bytes"
```

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Or:

```bash
mvn clean package
java -jar target/resource-booking-system-1.0.0.jar
```

## Seed accounts

The application creates these accounts automatically if they do not exist:

- ADMIN: `admin` / `Admin@123`
- USER: `user` / `User@123`

Passwords are stored with BCrypt.

## Swagger

Open:

`http://localhost:8080/swagger-ui.html`

OpenAPI JSON:

`http://localhost:8080/v3/api-docs`

## Authentication

Login:

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "user",
  "password": "User@123"
}
```

Then send:

```http
Authorization: Bearer <JWT>
```

## Main endpoints

### Authentication

- `POST /api/auth/login` public

### Resources

- `GET /api/resources` ADMIN/USER
- `GET /api/resources/{id}` ADMIN/USER
- `POST /api/resources` ADMIN
- `PUT /api/resources/{id}` ADMIN
- `DELETE /api/resources/{id}` ADMIN

### Reservations

- `GET /api/reservations` ADMIN sees all; USER sees own
- `GET /api/reservations/{id}` ADMIN can see any; USER can see own
- `POST /api/reservations` ADMIN/USER
- `PUT /api/reservations/{id}` ADMIN can update any; USER can update own
- `DELETE /api/reservations/{id}` ADMIN can delete any; USER can delete own

## Reservation request

There is deliberately no `userId` or `price` field. The authenticated user is taken from the JWT, and the reservation price is taken from the selected resource on the server.

```json
{
  "resourceId": 1,
  "startTime": "2026-09-10T10:00:00",
  "endTime": "2026-09-10T12:00:00"
}
```

The server rejects reservations whose start time is in the past and requires `startTime` to be before `endTime`. The server also prevents overlapping bookings.

## Filtering / pagination / sorting

```http
GET /api/reservations?status=CONFIRMED&minPrice=100&maxPrice=1000&page=0&size=10&sort=price,desc
```

Supported filters:

- `status`
- `minPrice`
- `maxPrice`
- `page`
- `size`
- `sort`

Examples:

```http
GET /api/reservations?page=0&size=10
GET /api/reservations?status=PENDING
GET /api/reservations?minPrice=100&maxPrice=500
GET /api/reservations?sort=startTime,asc
```

## Security behavior

A USER cannot provide another user's ID to obtain their reservations because the reservation create DTO has no user ID and reservation reads/updates/deletes verify ownership against the authenticated principal.

## HTTP responses

- `200 OK` successful read/update
- `201 Created` successful creation
- `204 No Content` successful deletion
- `400 Bad Request` validation/business-rule error
- `401 Unauthorized` missing/invalid JWT
- `403 Forbidden` insufficient role
- `404 Not Found` entity does not exist
- `409 Conflict` reservation overlap/conflict

## Project structure

```text
controller -> service -> repository
                 |
                 +-> security
                 +-> DTO
                 +-> exception
```


## Environment variables

Required:

- `DB_USERNAME` - MySQL username
- `DB_PASSWORD` - MySQL password
- `JWT_SECRET` - JWT signing secret (at least 32 bytes/characters)

Optional:

- `DB_URL` - defaults to the local `resource_booking` MySQL database
- `JWT_EXPIRATION_MS` - defaults to `86400000`

Example PowerShell setup:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="use-a-long-random-secret-at-least-32-characters"
mvn clean test
mvn spring-boot:run
```

Never commit real values for `DB_PASSWORD` or `JWT_SECRET`.
