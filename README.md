# 🍔 Hunger Killer — Food Ordering Microservices

A microservices-based food ordering backend built with **Java 21** and **Spring Boot 4.0.6**. Three independent services handle customer registration, menu management, and cart operations — communicating through REST and Apache Kafka.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          Client / API                           │
└──────────┬──────────────────────┬───────────────────────────────┘
           │ REST                 │ REST                │ REST
  ┌────────▼────────┐   ┌────────▼────────┐   ┌────────▼────────┐
  │ Customer Service│   │  Menu Service   │   │  Cart Service   │
  │   Port: 8081    │   │   Port: 8082    │   │   Port: 8083    │
  │   H2: customerdb│   │   H2: menudb    │   │   H2: cartdb    │
  └────────┬────────┘   └────────▲────────┘   └────────┬────────┘
           │                     │ REST (sync)          │
           │ Kafka               └──────────────────────┘
           ▼                  Cart validates items against Menu
    [customer.registered]
    Topic (fire-and-forget)
```

**Communication patterns:**
- **Cart → Menu**: Synchronous REST call — Cart fetches item price and availability from Menu before accepting an item into the cart.
- **Customer → Kafka**: Asynchronous event — On registration, Customer publishes a `CustomerRegisteredEvent` to the `customer.registered` topic (fire-and-forget). Future consumers (e.g. a Notification service) can subscribe without Customer knowing about them.

---

## 🧩 Services

### 1. Customer Service
Manages customer accounts and addresses. Publishes registration events to Kafka.

| Detail | Value |
|---|---|
| Port | `8081` |
| Database | H2 in-memory (`customerdb`) |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| H2 Console | http://localhost:8081/h2-console |
| Kafka topic (producer) | `customer.registered` |

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/customers/` | Health check / hello |
| `POST` | `/api/v1/customers/register` | Register a new customer |
| `GET` | `/api/v1/customers/{customerId}` | Get customer by ID |

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

| Detail | Value |
|---|---|
| Port | `8082` |
| Database | H2 in-memory (`menudb`) |
| Swagger UI | http://localhost:8082/swagger-ui.html |
| H2 Console | http://localhost:8082/h2-console |

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/menu/items/{itemId}` | Get item by ID (called by Cart Service) |
| `GET` | `/api/v1/menu/restaurants/{restaurantId}/items` | Get all items for a restaurant |
| `POST` | `/api/v1/menu/items` | Create a new menu item |

**Create item request body:**
```json
{
  "name": "Shawarma Plate",
  "description": "Chicken shawarma with garlic sauce",
  "price": 12.50,
  "imageUrl": "https://example.com/shawarma.jpg",
  "categoryId": "category-uuid"
}
```

---

### 3. Cart Service
Manages shopping carts per customer. Calls Menu Service synchronously to validate item price and availability before adding to cart.

| Detail | Value |
|---|---|
| Port | `8083` |
| Database | H2 in-memory (`cartdb`) |
| Swagger UI | http://localhost:8083/swagger-ui.html |
| H2 Console | http://localhost:8083/h2-console |
| Menu MS URL (local) | `http://localhost:8082` |

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/cart/{customerId}/items` | Add item to cart |
| `GET` | `/api/v1/cart/{customerId}` | Get active cart |
| `DELETE` | `/api/v1/cart/{customerId}/items/{cartItemId}` | Remove a specific item |
| `DELETE` | `/api/v1/cart/{customerId}` | Clear entire cart |

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

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Database | H2 in-memory (per service, no shared state) |
| Messaging | Apache Kafka (`spring-kafka`) |
| Inter-service REST | `RestTemplate` (Cart → Menu) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Boilerplate reduction | Lombok |
| Containerization | Docker + Docker Compose |

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
- Menu → http://localhost:8003  *(mapped from internal 8082)*
- Cart → http://localhost:8002  *(mapped from internal 8083)*

> **Note:** The `docker-compose.yml` does not include a Kafka container yet. Customer Service will start but the Kafka producer will fail unless you add a Kafka service to the compose file or run one separately.

To stop everything:
```bash
docker-compose down
```

---

### Option B — Run Locally with Maven

Start **Menu Service first** since Cart depends on it.

```bash
# Terminal 1 — Menu Service (must start first)
cd menu
./mvnw spring-boot:run

# Terminal 2 — Customer Service (needs Kafka on localhost:9092)
cd Customer
./mvnw spring-boot:run

# Terminal 3 — Cart Service
cd cart
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
    url: http://localhost:8082   # change to http://menu:8080 inside Docker
```

---

## 📄 API Documentation

Each service exposes a full Swagger UI once running:

| Service | Swagger UI | API Docs (JSON) |
|---|---|---|
| Customer | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| Menu | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |
| Cart | http://localhost:8083/swagger-ui.html | http://localhost:8083/api-docs |

---

## 📁 Project Structure

```
hunger-killer/
├── Customer/                          # Customer microservice
│   ├── src/main/java/com/hungerkiller/customer/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic
│   │   ├── model/                     # Customer, Address entities
│   │   ├── dto/                       # Request/Response DTOs
│   │   ├── repository/                # Spring Data JPA
│   │   └── kafka/                     # Kafka producer + event model
│   ├── Dockerfile
│   └── pom.xml
│
├── menu/                              # Menu microservice
│   ├── src/main/java/com/hungerkiller/menu/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic
│   │   ├── model/                     # MenuItem, Category,  Restaur
│   │   ├── dto/
│   │   └── repository/
│   ├── Dockerfile
│   └── pom.xml
│
├── cart/                              # Cart microservice
│   ├── src/main/java/com/hungerkiller/cart/
│   │   ├── controller/                # REST endpoints
│   │   ├── service/                   # Business logic + Menu validation
│   │   ├── client/                    # MenuServiceClient (REST)
│   │   ├── model/                     # Cart, CartItem entities
│   │   ├── dto/
│   │   └── repository/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml
└── .dockerignore
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
