# Hac Book API

This is the backend API for **Hac Book** – a personal household accounting application.

It is built as a RESTful API using Spring Boot, designed with a layered architecture (Controller / Service / Repository) and a clear separation from the frontend (React / TypeScript / Next.js).

The application supports secure authentication and authorization using JWT with HttpOnly cookies, and is backed by a PostgreSQL database.

It includes comprehensive testing (unit, slice, and integration tests with Testcontainers) and a fully automated CI/CD pipeline using Docker and GitHub Actions for deployment to AWS EC2, ensuring production-like reliability.

Frontend repository 👉 [Hac Book Web](https://github.com/katsu0511/hac_book_web)

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- JPA / Hibernate
- JWT authentication

### Database

- PostgreSQL

### Testing

- JUnit 5
- Mockito
- Testcontainers
- Spring Test (@WebMvcTest)

### Infrastructure

- AWS EC2
- Docker
- GitHub Actions (CI/CD)
- Gradle

## System Configuration

- Frontend: Next.js (Vercel)
- Backend: Spring Boot (Dockerized, deployed on AWS EC2)
- Database: PostgreSQL (Docker)
- Authentication: JWT + HttpOnly Cookie

```
+----------------+       +----------------+       +----------------------+       +----------------------+
|    Browser     | ----> |    Next.js     | ----> |   Spring Boot API    | ----> |      PostgreSQL      |
|                | <---- |    (Vercel)    | <---- |  (Docker / AWS EC2)  | <---- |  (Docker / AWS EC2)  |
+----------------+       +----------------+       +----------------------+       +----------------------+
```

The backend and database are containerized using Docker to ensure consistent environments across development, testing, and production.

## Main Functions

- User registration / login / logout (JWT authentication)
- Category Management
  - Parent-Child category support
  - Create and edit user's categories
- Transaction (Income / Expense) management
  - Retrieve transactions for a specified period
  - Create, edit and delete user's income and expense records
- Dashboard
  - Total Income / Expense
  - Expense breakdown for each category

## Database Configuration

### Database ER Diagram

The ER diagram below shows the database schema of this application,
including user-specific data separation and category relationships.

![ER Diagram](docs/er-diagram.png)

Default categories are initialized from category_templates when a user is created.

### Tables

- users
- settings
- category_templates
- categories
- transactions

The database design considers data separation for each user.

## API Configuration

All endpoints require authentication unless otherwise specified, and enforce user-level data isolation.

| Method | Path | Description |
|--------|------|-------------|
| GET | /check-auth | Check authentication |
| POST | /login | Login |
| POST | /logout | Logout |
| POST | /signup | Register |
| GET | /categories | List categories |
| GET | /parent-categories | List parent categories |
| GET | /categories/{id} | Get category |
| POST | /categories | Create category |
| POST | /categories/{id} | Update category |
| GET | /transactions | List transactions |
| GET | /transactions/{id} | Get transaction |
| POST | /transactions | Create transaction |
| POST | /transactions/{id} | Update transaction |
| DELETE | /transactions/{id} | Delete transaction |
| GET | /dashboard/summary | Get summary |

## Architecture

- Layered architecture (Controller / Service / Repository)
- DTO pattern used to separate API and domain models
- Validation handled at both API and database levels

## Authentication and Authorization

- Token-based authentication using JWT
- Tokens are stored in HttpOnly cookies
- User ownership is checked in the Service layer to prevent access to other users' data

## Testing

The application includes multiple layers of testing to ensure reliability and maintainability.

- Unit Testing
  - JUnit 5 + Mockito
  - Focused on business logic mainly in the Service layer

- Slice Testing
  - Controller tests using @WebMvcTest
  - Repository tests using Spring Data JPA with Testcontainers (PostgreSQL)

- Integration Testing
  - End-to-end testing using full Spring Boot context
  - Testcontainers + Docker used to run real PostgreSQL instances
  - Flyway used to apply database schema in tests

- Test Coverage
  - Over 80% coverage across core modules

## CI/CD

CI/CD pipeline is fully automated using GitHub Actions.

- Build Docker image on push
- Run automated tests (unit, slice, integration)
- Push image to Docker Hub
- Deploy automatically to AWS EC2

This ensures consistent deployments and prevents regressions.

## Deployment

- Backend is containerized using Docker
- Deployed on AWS EC2
- CI/CD pipeline automatically builds, tests, and deploys the application

## Future Improvement

- Document API by OpenAPI (Swagger)
- Expand authorization management (e.g. administrator role)
