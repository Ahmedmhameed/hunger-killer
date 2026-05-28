# Hunger Killer - Kitchen Service

This is the Kitchen Microservice for the Hunger Killer food ordering platform. It handles kitchen order management, ticket processing, and kitchen workflow coordination.

## Features

- **Kitchen Ticket Management**: Create and manage kitchen tickets from orders
- **Order Event Consumption**: Listens to `order.placed` events and creates kitchen tickets
- **Status Updates**: Track kitchen ticket status from NEW → IN_PREPARATION → READY
- **Kitchen Station Management**: Manage kitchen stations and workload distribution
- **Event Publishing**: Publishes `kitchen.status` events for downstream services
- **Service Discovery**: Auto-registers with Eureka service discovery
- **API Documentation**: Swagger/OpenAPI 3.0 integration
- **Monitoring**: Spring Boot Actuator with Prometheus metrics

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
docker-compose up -d
```

### 2. Build the Service

```bash
mvn clean package -DskipTests
```

### 3. Run the Service

```bash
mvn spring-boot:run
```

Or using the built JAR:

```bash
java -jar target/kitchen-service-1.0.0.jar
```

### 4. Access the Service

- **API Base URL**: `http://localhost:8087`
- **Swagger UI**: `http://localhost:8087/swagger-ui.html`
- **Health Check**: `http://localhost:8087/actuator/health`
- **Metrics**: `http://localhost:8087/actuator/prometheus`

## API Endpoints

All endpoints require JWT authentication (except Swagger and health endpoints).

### Kitchen Tickets

- **GET** `/api/v1/kitchen/tickets/{ticketId}` - Get ticket details
- **GET** `/api/v1/kitchen/tickets/restaurant/{restaurantId}` - List active tickets by restaurant
- **PATCH** `/api/v1/kitchen/tickets/{ticketId}/status` - Update ticket status
  - **Request**: `KitchenStatusUpdateRequest` (status: NEW | IN_PREPARATION | READY | CANCELED)
  - **Response**: `KitchenTicketResponse`

## Configuration

Edit `src/main/resources/application.yaml`:

```yaml
server:
  port: 8087

spring:
  application:
    name: kitchen-service
  datasource:
    url: jdbc:mysql://localhost:3306/kitchen_db
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
    expiration: 86400000
```

## Kafka Events

### Consumes: order.placed

Topic: `order.placed`

Creates a new `KitchenTicket` when an order is placed. Automatically assigns items to kitchen stations based on item type.

### Produces: kitchen.status

Topic: `kitchen.status`

Published when ticket status changes:

```json
{
  "ticketId": "UUID",
  "orderId": "string",
  "status": "IN_PREPARATION|READY|NEW|CANCELED",
  "estimatedReadyAt": "timestamp",
  "actualReadyAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## Database Schema

Flyway automatically creates and manages the database schema:

- **kitchen_tickets**: Stores kitchen order tickets with status tracking
- **kitchen_ticket_items**: Line items for each ticket (1:N relationship)
- **kitchen_stations**: Kitchen stations (GRILL, COLD_PREP, FRYER, PASTRY, DRINKS)

## Station Types

Items are auto-assigned to stations based on type:

- **GRILL**: Grilled items, steaks, burgers
- **COLD_PREP**: Salads, cold items
- **FRYER**: Fried items
- **PASTRY**: Baked goods, pastries, bread
- **DRINKS**: Beverages, juices, coffee

## Testing

```bash
# Run tests with Testcontainers
mvn test

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

## Building Docker Image

```bash
# Build Docker image
docker build -t kitchen-service:1.0.0 .

# Run container
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/kitchen_db \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka \
  -p 8087:8087 \
  kitchen-service:1.0.0
```

## Security

- **JWT Tokens**: 24-hour expiration, signed with HS512
- **Stateless Authentication**: All requests require Bearer token
- **CORS**: Configured for microservice communication

## Technologies

- Spring Boot 3.3.3
- Spring Cloud (Eureka, OpenFeign, Resilience4j)
- Spring Security + JWT (JJWT)
- Spring Data JPA + Hibernate
- MySQL 8.0
- Apache Kafka
- MapStruct
- Lombok
- SpringDoc OpenAPI 3.0
- Prometheus + Micrometer
- Zipkin Distributed Tracing
- Flyway Database Migrations
