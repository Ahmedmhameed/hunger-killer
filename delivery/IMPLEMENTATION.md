# Delivery Service Implementation - Mock

This document describes the mock implementation of the Delivery Microservice for the Hunger Killer Food Ordering System.

## Overview

The Delivery Service is a **mock implementation** that simulates a real delivery management system without actually connecting to external delivery platforms or real GPS tracking systems. It provides REST endpoints to manage delivery lifecycle from assignment to completion.

## Key Features

### 1. **Mock Delivery Assignment**

- When a delivery is requested, the service simulates assigning a random driver from a pool of mock drivers
- Each driver has a randomly generated phone number
- Vehicle information is mocked (cars, bikes, vans)
- Estimated delivery time is calculated as 30-45 minutes from order time

### 2. **Mock GPS Tracking**

- The service stores GPS coordinates for:
  - Current driver location (starts at Gaza City coordinates: 31.9454°N, 35.2338°E - representing restaurant)
  - Destination coordinates (customer delivery address)
- Endpoints allow updating driver location (simulating real-time tracking)

### 3. **Delivery Status Management**

The service supports the following delivery statuses:

- **PENDING**: Delivery order created, waiting for assignment
- **ASSIGNED**: Driver assigned to the order
- **PICKING_UP**: Driver is picking up the order from restaurant
- **IN_TRANSIT**: Order is on the way to customer
- **ARRIVED**: Driver has arrived at delivery location
- **DELIVERED**: Order delivered successfully
- **FAILED**: Delivery failed
- **CANCELED**: Delivery cancelled

### 4. **Customer Feedback & Rating**

- After delivery completion, customers can submit:
  - Delivery feedback (text)
  - Rating (1-5 stars)
- Ratings are stored in the database for analytics

### 5. **Data Persistence**

- All delivery records are stored in an H2 in-memory database (`deliverydb`)
- Each delivery record includes:
  - Order ID (unique)
  - Customer ID
  - Delivery address and GPS coordinates
  - Driver information
  - Status history
  - Timestamps

## Database Schema

```sql
CREATE TABLE deliveries (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    driver_id VARCHAR(36),
    driver_name VARCHAR(100),
    driver_phone VARCHAR(20),
    vehicle_number VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    current_latitude DECIMAL(10,8),
    current_longitude DECIMAL(11,8),
    delivery_latitude DECIMAL(10,8),
    delivery_longitude DECIMAL(11,8),
    estimated_delivery_time TIMESTAMP,
    actual_delivery_time TIMESTAMP,
    delivery_notes TEXT,
    customer_feedback TEXT,
    rating INT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
```

## REST API Endpoints

### Base URL: `/api/v1/deliveries`

| Method  | Endpoint                 | Description                      |
| ------- | ------------------------ | -------------------------------- |
| `GET`   | `/`                      | Health check                     |
| `POST`  | `/request`               | Request a new delivery           |
| `GET`   | `/{deliveryId}`          | Get delivery by ID               |
| `GET`   | `/order/{orderId}`       | Get delivery by order ID         |
| `GET`   | `/customer/{customerId}` | Get all deliveries for customer  |
| `PATCH` | `/{deliveryId}/status`   | Update delivery status           |
| `PATCH` | `/{deliveryId}/location` | Update driver's current location |
| `POST`  | `/{deliveryId}/complete` | Complete delivery with feedback  |
| `POST`  | `/{deliveryId}/cancel`   | Cancel delivery                  |
| `GET`   | `/status/{status}`       | Get all deliveries by status     |

## Request/Response Examples

### Request a Delivery

**Request:**

```json
{
  "orderId": "order-123",
  "customerId": "customer-456",
  "deliveryAddress": "123 Main St, Gaza",
  "deliveryLatitude": 31.9454,
  "deliveryLongitude": 35.2338,
  "deliveryNotes": "Please ring the bell twice"
}
```

**Response (201 Created):**

```json
{
  "code": 201,
  "message": "Delivery requested successfully",
  "data": {
    "id": "delivery-uuid",
    "orderId": "order-123",
    "customerId": "customer-456",
    "deliveryAddress": "123 Main St, Gaza",
    "status": "ASSIGNED",
    "driverId": "drv-001",
    "driverName": "Ahmed Driver",
    "driverPhone": "+972-59-1234567",
    "vehicleNumber": "CAR-101",
    "currentLatitude": 31.9454,
    "currentLongitude": 35.2338,
    "deliveryLatitude": 31.9454,
    "deliveryLongitude": 35.2338,
    "estimatedDeliveryTime": "2026-04-25T18:35:00",
    "deliveryNotes": "Please ring the bell twice",
    "createdAt": "2026-04-25T18:05:00",
    "updatedAt": "2026-04-25T18:05:00"
  }
}
```

### Update Delivery Status

**Request:**

```
PATCH /api/v1/deliveries/{deliveryId}/status?status=IN_TRANSIT
```

**Response (200 OK):**

```json
{
  "code": 200,
  "message": "Delivery status updated",
  "data": {
    "id": "delivery-uuid",
    "status": "IN_TRANSIT",
    "currentLatitude": 31.9454,
    "currentLongitude": 35.2338,
    "updatedAt": "2026-04-25T18:15:00"
  }
}
```

### Update Driver Location (GPS Tracking Simulation)

**Request:**

```json
{
  "latitude": 31.948,
  "longitude": 35.236
}
```

**Response:**

```json
{
  "code": 200,
  "message": "Driver location updated",
  "data": {
    "id": "delivery-uuid",
    "currentLatitude": 31.948,
    "currentLongitude": 31.236,
    "updatedAt": "2026-04-25T18:20:00"
  }
}
```

### Complete Delivery with Feedback

**Request:**

```
POST /api/v1/deliveries/{deliveryId}/complete?feedback=Great service!&rating=5
```

**Response:**

```json
{
  "code": 200,
  "message": "Delivery completed",
  "data": {
    "id": "delivery-uuid",
    "status": "DELIVERED",
    "actualDeliveryTime": "2026-04-25T18:35:00",
    "customerFeedback": "Great service!",
    "rating": 5,
    "updatedAt": "2026-04-25T18:35:00"
  }
}
```

## Mock Data

### Mock Drivers

```
- Ahmed Driver (drv-001) - Vehicle: CAR-101
- Mohammed Delivery (drv-002) - Vehicle: CAR-202
- Hassan Rider (drv-003) - Vehicle: BIKE-303
- Karim Express (drv-004) - Vehicle: CAR-404
- Fatima Courier (drv-005) - Vehicle: VAN-505
```

### Mock Initial Location

- Latitude: 31.9454°N
- Longitude: 35.2338°E
- Represents Gaza City (restaurant location)

## Configuration

### Application Properties (`application.yaml`)

```yaml
server:
  port: 8086

spring:
  datasource:
    url: jdbc:h2:mem:deliverydb
  jpa:
    hibernate:
      ddl-auto: create-drop

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```

## Access Points

| Resource        | URL                                      |
| --------------- | ---------------------------------------- |
| Swagger UI      | http://localhost:8086/swagger-ui.html    |
| API Docs (JSON) | http://localhost:8086/api-docs           |
| H2 Console      | http://localhost:8086/h2-console         |
| Health Check    | http://localhost:8086/api/v1/deliveries/ |

## Technology Stack

- **Framework**: Spring Boot 3.3.3
- **Language**: Java 21
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Validation**: Jakarta Bean Validation
- **Boilerplate Reduction**: Lombok

## Limitations (by design)

This is a mock implementation, so it:

- ✅ Does NOT connect to real delivery systems
- ✅ Does NOT use real GPS services
- ✅ Does NOT send real-time notifications
- ✅ Does NOT integrate with external maps APIs
- ✅ Does NOT connect to payment gateways

The service is designed for **testing and demo purposes** within the Hunger Killer ecosystem.

## Future Enhancements

For production use, this service could be extended with:

1. Real GPS integration and live tracking
2. Integration with delivery partner APIs (Uber Eats Delivery, etc.)
3. Notifications (SMS/push) for driver and customer
4. Route optimization algorithms
5. Delivery analytics and reporting
6. Integration with mapping APIs (Google Maps, etc.)
7. Real-time updates via WebSockets or Server-Sent Events

---

**Implementation Date**: April 2026  
**Version**: 1.0.0  
**Status**: Mock - For Testing Only
