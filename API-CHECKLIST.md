# Evaluation Checklist

## Authentication
- [x] POST /api/auth/login
- [x] JWT validation
- [x] BCrypt

## RBAC
- [x] USER reads resources
- [x] ADMIN CRUD resources
- [x] ADMIN sees all reservations
- [x] USER sees own reservations

## Reservations
- [x] PENDING/CONFIRMED/CANCELLED
- [x] BigDecimal price
- [x] Ownership from JWT
- [x] Time validation
- [x] Overlap prevention

## Querying
- [x] status
- [x] minPrice
- [x] maxPrice
- [x] page
- [x] size
- [x] sort

## API quality
- [x] DTOs
- [x] global exception handling
- [x] Swagger
- [x] Postman
- [x] seed data
- [x] tests
