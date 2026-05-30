# 🍔 Hunger Killer — Food Ordering Microservices

A microservices-based food ordering backend built with **Java 21** and **Spring Boot 3.3.3**. Multiple independent services handle customer registration, menu management, cart operations, order processing, and payment handling — communicating through REST and Apache Kafka.

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                            Client / API / Gateway                         │
└────────┬──────────────┬──────────────┬──────────────┬────────────────────┘
         │ REST         │ REST         │ REST         │ REST               │ REST
┌────────▼─────┐ ┌─────▼─────┐ ┌──────▼─────┐ ┌──────▼─────┐ ┌─────────▼────────┐
│  Customer    │ │   Menu    │ │    Cart    │ │   Order    │ │    Payment       │
│  Service     │ │  Service  │ │  Service   │ │  Service   │ │    Service       │
│ Port: 8081   │ │ Port:8082 │ │ Port:8083  │ │ Port: 8080 │ │   Port: 8084     │
│ H2: customer │ │ H2: menu  │ │ H2: cart   │ │ MySQL: ord │ │ H2: payment      │
└────────┬─────┘ └──────▲────┘ └──────┬─────┘ └──────┬─────┘ └────────▲─────────┘
         │              │ REST        │ REST        │                │
         │ Kafka        └─────────────┼────────────┘                │ REST
         ▼              (sync)        │ Kafka                        │ (payment
  [customer.registered]              ▼                              │  processing)
  Topic                    [order.created]
  (fire-and-forget)        [order.confirmed]
                           Topics (async)
```

**Communication patterns:**

- **Cart → Menu**: Synchronous REST call — Cart fetches item price and availability from Menu before accepting an item.
- **Order → Menu & Cart**: Synchronous REST calls — Order validates item availability and retrieves pricing information.
- **Order → Payment**: Synchronous REST call — Order sends payment requests to Payment Service for processing.
- **Customer → Kafka**: Asynchronous event — On registration, Customer publishes `CustomerRegisteredEvent` (fire-and-forget).
- **Order → Kafka**: Asynchronous events — Order publishes `OrderCreatedEvent` and `OrderConfirmedEvent` for future consumers (e.g., Kitchen, Delivery, Notification services).

---

## 🧩 Services

### 1. Customer Service

Manages customer accounts and addresses. Publishes registration events to Kafka.

| Detail                 | Value                                 |
| ---------------------- | ------------------------------------- |
| Port                   | `8081`                                |
| Database               | H2 in-memory (`customerdb`)           |
| Swagger UI             | http://localhost:8081/swagger-ui.html |
| H2 Console             | http://localhost:8081/h2-console      |
| Kafka topic (producer) | `customer.registered`                 |

**Endpoints:**

| Method | Path                             | Description             |
| ------ | -------------------------------- | ----------------------- |
| `GET`  | `/api/v1/customers/`             | Health check / hello    |
| `POST` | `/api/v1/customers/register`     | Register a new customer |
| `GET`  | `/api/v1/customers/{customerId}` | Get customer by ID      |

**Register request body:**

```json
{
  "firstName": "Ahmed",
  "lastName": "Shorafa",
  "email": "ahmed@example.com",
  "phone": "+972-59-000-0000",
  "password": "securepass123",
  "dateOfBirth": "2000-01-15",
  "dietaryNotes": "No pork",
  "addresses": [
    {
      "label": "Home",
      "street": "Al-Nasser St.",
      "city": "Gaza",
      "gpsLat": 31.5,
      "gpsLng": 34.46
    }
  ]
}
```

**Kafka event published on registration:**

```json
{
  "customerId": "uuid",
  "firstName": "Ahmed",
  "lastName": "Shorafa",
  "email": "ahmed@example.com",
  "loyaltyTier": "BRONZE",
  "registeredAt": "2026-04-25T18:00:00"
}
```

---

### 2. Menu Service

Manages restaurants, categories, and food items. Serves item data to the Cart Service on demand.

| Detail     | Value                                 |
| ---------- | ------------------------------------- |
| Port       | `8082`                                |
| Database   | H2 in-memory (`menudb`)               |
| Swagger UI | http://localhost:8082/swagger-ui.html |
| H2 Console | http://localhost:8082/h2-console      |

**Endpoints:**

| Method | Path                                            | Description                             |
| ------ | ----------------------------------------------- | --------------------------------------- |
| `GET`  | `/api/v1/menu/items/{itemId}`                   | Get item by ID (called by Cart Service) |
| `GET`  | `/api/v1/menu/restaurants/{restaurantId}/items` | Get all items for a restaurant          |
| `POST` | `/api/v1/menu/items`                            | Create a new menu item                  |

**Create item request body:**

```json
{
  "name": "Shawarma Plate",
  "description": "Chicken shawarma with garlic sauce",
  "price": 12.5,
  "imageUrl": "https://example.com/shawarma.jpg",
  "categoryId": "category-uuid"
}
```

---

### 3. Cart Service

Manages shopping carts per customer. Calls Menu Service synchronously to validate item price and availability before adding to cart.

| Detail              | Value                                 |
| ------------------- | ------------------------------------- |
| Port                | `8083`                                |
| Database            | H2 in-memory (`cartdb`)               |
| Swagger UI          | http://localhost:8083/swagger-ui.html |
| H2 Console          | http://localhost:8083/h2-console      |
| Menu MS URL (local) | `http://localhost:8082`               |

**Endpoints:**

| Method   | Path                                           | Description            |
| -------- | ---------------------------------------------- | ---------------------- |
| `POST`   | `/api/v1/cart/{customerId}/items`              | Add item to cart       |
| `GET`    | `/api/v1/cart/{customerId}`                    | Get active cart        |
| `DELETE` | `/api/v1/cart/{customerId}/items/{cartItemId}` | Remove a specific item |
| `DELETE` | `/api/v1/cart/{customerId}`                    | Clear entire cart      |

**Add item request body:**

```json
{
  "itemId": "menu-item-uuid",
  "quantity": 2,
  "specialNotes": "Extra sauce please"
}
```

> When adding an item, Cart Service calls `GET /api/v1/menu/items/{itemId}` on Menu Service. If the item doesn't exist or `isAvailable` is `false`, the request is rejected with `400 Bad Request`.

---

### 4. Payment Service

Mock payment processing service. Simulates payment transactions without connecting to real payment gateways. Stores payment records in H2 database.

| Detail     | Value                                 |
| ---------- | ------------------------------------- |
| Port       | `8084`                                |
| Database   | H2 in-memory (`paymentdb`)            |
| Swagger UI | http://localhost:8084/swagger-ui.html |
| H2 Console | http://localhost:8084/h2-console      |

**Endpoints:**

| Method | Path                                     | Description                                 |
| ------ | ---------------------------------------- | ------------------------------------------- |
| `GET`  | `/api/v1/payments/`                      | Health check / hello                        |
| `POST` | `/api/v1/payments/process`               | Process a new payment (Mock - 80% approval) |
| `GET`  | `/api/v1/payments/{paymentId}`           | Get payment by ID                           |
| `GET`  | `/api/v1/payments/order/{orderId}`       | Get payment by order ID                     |
| `GET`  | `/api/v1/payments/customer/{customerId}` | Get all payments for customer               |
| `POST` | `/api/v1/payments/{paymentId}/refund`    | Refund an approved payment                  |
| `GET`  | `/api/v1/payments/{paymentId}/status`    | Check payment status                        |

**Process payment request body:**

```json
{
  "orderId": "order-uuid",
  "customerId": "customer-uuid",
  "amount": 45.99,
  "paymentMethod": "CREDIT_CARD",
  "description": "Order payment",
  "cardNumber": "4111111111111111",
  "cardHolderName": "Ahmed Shorafa",
  "expiryDate": "12/25",
  "cvv": "123"
}
```

**Mock Implementation:**

- **Success Rate**: 80% of payments are approved, 20% are rejected
- **Transaction Reference**: Generated in format `MOCK_TXN_XXXXXXXX`
- **Sensitive Data**: Card numbers are not stored; only last 4 digits are saved
- **Payment Methods**: CREDIT_CARD, DEBIT_CARD, CASH_ON_DELIVERY, DIGITAL_WALLET
- **Payment Status**: PENDING, APPROVED, REJECTED, FAILED, REFUNDED

---

### 5. Delivery Service (Mock)

Mock delivery tracking service. Simulates delivery assignment, GPS tracking, and delivery completion without connecting to real delivery platforms. Stores delivery records in H2 database.

| Detail     | Value                                 |
| ---------- | ------------------------------------- |
| Port       | `8086`                                |
| Database   | H2 in-memory (`deliverydb`)           |
| Swagger UI | http://localhost:8086/swagger-ui.html |
| H2 Console | http://localhost:8086/h2-console      |

**Endpoints:**

| Method  | Path                                       | Description                     |
| ------- | ------------------------------------------ | ------------------------------- |
| `GET`   | `/api/v1/deliveries/`                      | Health check / hello            |
| `POST`  | `/api/v1/deliveries/request`               | Request a new delivery (Mock)   |
| `GET`   | `/api/v1/deliveries/{deliveryId}`          | Get delivery by ID              |
| `GET`   | `/api/v1/deliveries/order/{orderId}`       | Get delivery by order ID        |
| `GET`   | `/api/v1/deliveries/customer/{customerId}` | Get all deliveries for customer |
| `PATCH` | `/api/v1/deliveries/{deliveryId}/status`   | Update delivery status          |
| `PATCH` | `/api/v1/deliveries/{deliveryId}/location` | Update driver's location (GPS)  |
| `POST`  | `/api/v1/deliveries/{deliveryId}/complete` | Complete delivery with feedback |
| `POST`  | `/api/v1/deliveries/{deliveryId}/cancel`   | Cancel delivery                 |
| `GET`   | `/api/v1/deliveries/status/{status}`       | Get deliveries by status        |

**Request delivery request body:**

```json
{
  "orderId": "order-uuid",
  "customerId": "customer-uuid",
  "deliveryAddress": "123 Main St, Gaza",
  "deliveryLatitude": 31.9454,
  "deliveryLongitude": 35.2338,
  "deliveryNotes": "Please ring the bell twice"
}
```

**Update driver location request body:**

```json
{
  "latitude": 31.948,
  "longitude": 35.236
}
```

**Mock Implementation:**

- **Driver Assignment**: Randomly assigned from 5 mock drivers with vehicle information
- **GPS Tracking**: Stores current and destination coordinates (no real GPS)
- **Estimated Delivery Time**: Randomly calculated as 30-45 minutes from request time
- **Delivery Statuses**: PENDING → ASSIGNED → PICKING_UP → IN_TRANSIT → ARRIVED → DELIVERED
- **Customer Feedback**: Supports ratings (1-5 stars) and text feedback after delivery
- **Mock Initial Location**: Gaza City coordinates (31.9454°N, 35.2338°E)

---

| Layer                 | Technology                                  |
| --------------------- | ------------------------------------------- |
| Language              | Java 21                                     |
| Framework             | Spring Boot 4.0.6                           |
| Database              | H2 in-memory (per service, no shared state) |
| Messaging             | Apache Kafka (`spring-kafka`)               |
| Inter-service REST    | `RestTemplate` (Cart → Menu)                |
| Validation            | Jakarta Bean Validation                     |
| API Docs              | SpringDoc OpenAPI / Swagger UI              |
| Boilerplate reduction | Lombok                                      |
| Containerization      | Docker + Docker Compose                     |

---

## 🚀 Getting Started

### Prerequisites

- [Java 21+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [Docker & Docker Compose](https://www.docker.com/)
- A running **Kafka broker** on `localhost:9092` (required by Customer Service)

---

### Option A — Docker Compose

```bash
git clone https://github.com/Ahmedmhameed/hunger-killer.git
cd hunger-killer
docker-compose up --build
```

Services will be available at:

- Customer → http://localhost:8001
- Menu → http://localhost:8003 _(mapped from internal 8082)_
- Cart → http://localhost:8002 _(mapped from internal 8083)_

> **Note:** The `docker-compose.yml` does not include a Kafka container yet. Customer Service will start but the Kafka producer will fail unless you add a Kafka service to the compose file or run one separately.

To stop everything:

```bash
docker-compose down
```

---

### Option B — Run Locally with Maven

Start **Menu Service first** since Cart depends on it. Delivery Service is independent and can run in any order.

```bash
# Terminal 1 — Menu Service (must start first)
cd menu
./mvnw spring-boot:run

# Terminal 2 — Customer Service (needs Kafka on localhost:9092)
cd customer
./mvnw spring-boot:run

# Terminal 3 — Cart Service
cd cart
./mvnw spring-boot:run

# Terminal 4 — Payment Service
cd payment
./mvnw spring-boot:run

# Terminal 5 — Delivery Service (Mock)
cd delivery
./mvnw spring-boot:run
```

---

## ⚙️ Configuration

**Customer Service** (`Customer/src/main/resources/application.yaml`)

```yaml
server:
  port: 8081
spring:
  kafka:
    bootstrap-servers: localhost:9092
  datasource:
    url: jdbc:h2:mem:customerdb
```

**Menu Service** (`menu/src/main/resources/application.yml`)

```yaml
server:
  port: 8082
spring:
  datasource:
    url: jdbc:h2:mem:menudb
```

**Cart Service** (`cart/src/main/resources/application.yml`)

```yaml
server:
  port: 8083
spring:
  datasource:
    url: jdbc:h2:mem:cartdb
services:
  menu:
    url: http://localhost:8082
```

**Payment Service** (`payment/src/main/resources/application.yaml`)

```yaml
server:
  port: 8084
spring:
  datasource:
    url: jdbc:h2:mem:paymentdb
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Delivery Service** (`delivery/src/main/resources/application.yaml`)

```yaml
server:
  port: 8086
spring:
  datasource:
    url: jdbc:h2:mem:deliverydb
  jpa:
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true
      path: /h2-console
```

---

## 📄 API Documentation

Each service exposes a full Swagger UI once running:

| Service  | Swagger UI                            | API Docs (JSON)                |
| -------- | ------------------------------------- | ------------------------------ |
| Customer | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| Menu     | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |
| Cart     | http://localhost:8083/swagger-ui.html | http://localhost:8083/api-docs |
| Payment  | http://localhost:8084/swagger-ui.html | http://localhost:8084/api-docs |
| Delivery | http://localhost:8086/swagger-ui.html | http://localhost:8086/api-docs |

---

## 📁 Project Structure

```
hunger-killer/
├── customer/                          # Customer microservice
│   ├── src/main/java/com/ahmedkh/customer/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic
│   │   ├── entity/                    # Customer, Address entities
│   │   ├── dto/                       # Request/Response DTOs
│   │   ├── repository/                # Spring Data JPA
│   │   └── kafka/                     # Kafka producer + event model
│   ├── Dockerfile
│   └── pom.xml
│
├── menu/                              # Menu microservice
│   ├── src/main/java/com/ahmedkh/menu/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic
│   │   ├── entity/                    # MenuItem, Category, Restaurant
│   │   ├── dto/                       # DTOs
│   │   └── repository/                # Spring Data JPA
│   ├── Dockerfile
│   └── pom.xml
│
├── cart/                              # Cart microservice
│   ├── src/main/java/com/ahmedkh/cart/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic + Menu validation
│   │   ├── client/                    # MenuServiceClient (REST)
│   │   ├── entity/                    # Cart, CartItem entities
│   │   ├── dto/                       # DTOs
│   │   └── repository/                # Spring Data JPA
│   ├── Dockerfile
│   └── pom.xml
│
├── payment/                           # Payment microservice (Mock)
│   ├── src/main/java/com/ahmedkh/payment/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Mock payment processing logic
│   │   ├── entity/                    # Payment entity
│   │   ├── dto/                       # Request/Response DTOs
│   │   ├── repository/                # Spring Data JPA
│   │   └── exception/                 # Exception handling
│   ├── Dockerfile
│   ├── HELP.md
│   └── pom.xml
│
├── delivery/                          # Delivery microservice (Mock)
│   ├── src/main/java/com/ahmedkh/delivery/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Mock delivery tracking logic
│   │   ├── entity/                    # Delivery entity
│   │   ├── dto/                       # Request/Response DTOs
│   │   ├── repository/                # Spring Data JPA
│   │   ├── exception/                 # Exception handling
│   │   └── config/                    # Swagger & JPA config
│   ├── src/main/resources/
│   │   ├── application.yaml           # Configuration
│   │   └── db/migration/              # Database schema
│   ├── IMPLEMENTATION.md
│   ├── pom.xml
│   └── mvnw/mvnw.cmd
│
├── docker-compose.yml
├── .dockerignore
└── README.md
```

---

## 👤 Author

**Ahmed Yehea Fayez Shorafa**  
Information Technology Student — Islamic University of Gaza  
ID: 120211088  
GitHub: [@Ahmedmhameed](https://github.com/Ahmedmhameed)

---

## 📜 License

Developed for academic purposes — Advanced Software Engineering course (SDEV 4304).
