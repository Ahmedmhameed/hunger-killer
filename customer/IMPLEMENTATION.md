# Hunger Killer - Customer Service

This is the Customer Microservice for the Hunger Killer food ordering platform. It handles customer registration, authentication, profile management, and address management.

## Features

- **Customer Registration**: Register new customers with email and phone validation
- **Authentication**: JWT-based token generation and validation with 24-hour expiration
- **Profile Management**: View and update customer information
- **Address Management**: Add, view, and delete delivery addresses with GPS coordinates
- **Event Publishing**: Publishes `customer.registered` events to Kafka for downstream services
- **Service Discovery**: Auto-registers with Eureka service discovery
- **API Documentation**: Swagger/OpenAPI 3.0 integration for interactive API documentation
- **Monitoring**: Spring Boot Actuator with Prometheus metrics and Zipkin tracing

## Prerequisites

- **Java 21** (required)
- **Maven 3.8+**
- **MySQL 8.0+**
- **Kafka + Zookeeper**
- **Spring Cloud Eureka** (running on port 8761)
- **Confluent Schema Registry** (for Avro serialization)

## Quick Start

### 1. Start Infrastructure Services

```bash
# Start MySQL, Kafka, Zookeeper, and Schema Registry using Docker Compose
docker-compose up -d
```

### 2. Build the Service

```bash
# Build the Maven project
mvn clean package -DskipTests
```

### 3. Run the Service

```bash
# Run the application
mvn spring-boot:run
```

Or using the built JAR:

```bash
java -jar target/customer-service-1.0.0.jar
```

### 4. Access the Service

- **API Base URL**: `http://localhost:8081`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **Health Check**: `http://localhost:8081/actuator/health`
- **Metrics**: `http://localhost:8081/actuator/prometheus`

## API Endpoints

### Authentication

- **POST** `/api/v1/customers/register` - Register a new customer
  - **Request**: `RegisterRequest` (firstName, lastName, email, phone, password, dietaryNotes)
  - **Response**: `LoginResponse` (customerId, email, token)

- **POST** `/api/v1/customers/login` - Login customer
  - **Request**: `LoginRequest` (email, password)
  - **Response**: `LoginResponse` (customerId, email, token)

### Customer Profile (Requires JWT Token)

- **GET** `/api/v1/customers/{customerId}` - Get customer details
- **PUT** `/api/v1/customers/{customerId}` - Update customer profile
  - **Request**: `UpdateCustomerRequest` (firstName, lastName, phone, dietaryNotes)
- **DELETE** `/api/v1/customers/{customerId}` - Delete customer account (soft delete)

### Address Management (Requires JWT Token)

- **POST** `/api/v1/customers/{customerId}/addresses` - Add new address
  - **Request**: `AddressRequest` (label, street, city, gpsLat, gpsLng)
  - **Response**: `AddressResponse`

- **GET** `/api/v1/customers/{customerId}/addresses` - Get all addresses
  - **Response**: List of `AddressResponse`

- **DELETE** `/api/v1/customers/{customerId}/addresses/{addressId}` - Delete address

## Configuration

Edit `src/main/resources/application.yaml` to configure:

```yaml
server:
  port: 8081

spring:
  application:
    name: customer-service
  datasource:
    url: jdbc:mysql://localhost:3306/customer_db
    username: root
    password: root
  kafka:
    bootstrap-servers: localhost:9092

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

app:
  jwt:
    secret: your-secret-key-here
    expiration: 86400000 # 24 hours in milliseconds
```

## Database Schema

The service uses Flyway for database migrations:

- **customers table**: Stores customer profile information with soft delete support
- **addresses table**: Stores customer delivery addresses with GPS coordinates

Migrations are automatically executed on service startup.

## Events

### customer.registered (Produced)

Published when a customer successfully registers:

```json
{
  "customerId": "UUID",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "loyaltyPoints": 0,
  "createdAt": "timestamp"
}
```

## Security

- **JWT Tokens**: 24-hour expiration, signed with HS512 algorithm
- **Password Hashing**: BCrypt with strength 12
- **Stateless Authentication**: All requests require Bearer token in `Authorization` header
- **CORS**: Configured for microservice communication

### Example Authorization Header

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Running Tests

```bash
# Run all tests with Testcontainers
mvn test

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

## Building Docker Image

```bash
# Build Docker image
docker build -t customer-service:1.0.0 .

# Run container
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/customer_db \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka \
  -p 8081:8081 \
  customer-service:1.0.0
```

## Troubleshooting

### JWT Token Validation Fails

- Ensure `JWT_SECRET` environment variable is set and is at least 256 bits (32 bytes)
- Check token expiration with `exp` claim in JWT payload

### Kafka Connection Issues

- Verify Kafka is running: `docker ps | grep kafka`
- Check Schema Registry is accessible on `http://localhost:8081`
- Verify Zookeeper is running

### Database Connection Issues

- Ensure MySQL is running and database exists: `mysql -u root -proot -e "SHOW DATABASES;"`
- Check Flyway migration status in application logs
- Verify connection pool size and timeout settings

## Technologies

- Spring Boot 3.3.3
- Spring Cloud (Eureka, OpenFeign, Resilience4j)
- Spring Security + JWT (JJWT)
- Spring Data JPA + Hibernate
- MySQL 8.0
- Apache Kafka + Avro
- MapStruct
- Lombok
- SpringDoc OpenAPI 3.0 (Swagger)
- Prometheus + Micrometer
- Zipkin Distributed Tracing
- Flyway Database Migrations

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- [Kafka Documentation](https://kafka.apache.org/documentation/)

## License

This project is part of the Hunger Killer platform assignment for Advanced Software Engineering course.
