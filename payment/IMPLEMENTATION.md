# Payment Service Implementation

## Overview

The Payment Service is a **mock payment processing microservice** for the Hunger Killer food ordering system. It simulates payment transactions without connecting to real payment gateways.

## Architecture

### Database Schema

```sql
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    transaction_ref VARCHAR(100),
    description TEXT,
    processed_at TIMESTAMP,
    card_last_four VARCHAR(4),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Enums

**PaymentStatus:**

- `PENDING` - Payment waiting to be processed
- `APPROVED` - Payment successfully processed
- `REJECTED` - Payment declined
- `FAILED` - Payment processing error
- `REFUNDED` - Payment refunded

**PaymentMethod:**

- `CREDIT_CARD` - Credit card payment
- `DEBIT_CARD` - Debit card payment
- `CASH_ON_DELIVERY` - Cash on delivery
- `DIGITAL_WALLET` - Digital wallet payment

## Implementation Details

### Mock Payment Processing

The payment processing logic uses a **random simulation**:

```java
if (Math.random() < 0.8) {
    // 80% approval rate
    payment.setStatus(PaymentStatus.APPROVED);
    payment.setTransactionRef("MOCK_TXN_" + UUID.randomUUID().substring(0, 8));
} else {
    // 20% rejection rate
    payment.setStatus(PaymentStatus.REJECTED);
}
```

### Sensitive Data Handling

- **Card Numbers**: Not stored in database; only the last 4 digits are saved
- **CVV**: Never stored or logged
- **Cardholder Name**: Not stored (for this mock implementation)

### Service Methods

#### 1. **processPayment(ProcessPaymentRequest)**

- Creates a new payment record
- Simulates payment approval/rejection (80% success rate)
- Generates mock transaction reference
- Stores payment in H2 database
- Returns PaymentResponse with status

#### 2. **getPayment(UUID paymentId)**

- Retrieves payment by ID
- Throws exception if payment not found

#### 3. **getPaymentByOrderId(String orderId)**

- Retrieves payment associated with an order
- Useful for order verification

#### 4. **getPaymentsByCustomerId(String customerId)**

- Retrieves all payments for a customer
- Returns list of PaymentResponse objects

#### 5. **refundPayment(UUID paymentId)**

- Refunds an approved payment
- Updates status to REFUNDED
- Only works on approved payments

#### 6. **checkPaymentStatus(UUID paymentId)**

- Returns current payment status
- Used for status checking without full payment details

## API Endpoints

### POST /api/v1/payments/process

**Process a new payment**

Request:

```json
{
  "orderId": "order-123",
  "customerId": "cust-456",
  "amount": 45.99,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "cardHolderName": "Ahmed Shorafa",
  "expiryDate": "12/25",
  "cvv": "123"
}
```

Response (on success - 80%):

```json
{
  "success": true,
  "message": "Payment processed",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order-123",
    "customerId": "cust-456",
    "amount": 45.99,
    "paymentMethod": "CREDIT_CARD",
    "status": "APPROVED",
    "transactionRef": "MOCK_TXN_A1B2C3D4",
    "cardLastFour": "1111",
    "processedAt": "2026-04-25T18:00:00Z",
    "createdAt": "2026-04-25T18:00:00Z"
  }
}
```

### GET /api/v1/payments/{paymentId}

**Get payment by ID**

Response:

```json
{
  "success": true,
  "message": "Payment retrieved",
  "data": { ... }
}
```

### GET /api/v1/payments/order/{orderId}

**Get payment by order ID**

### GET /api/v1/payments/customer/{customerId}

**Get all payments for a customer**

Response:

```json
{
  "success": true,
  "message": "Payments retrieved",
  "data": [ ... ]
}
```

### POST /api/v1/payments/{paymentId}/refund

**Refund a payment**

Response:

```json
{
  "success": true,
  "message": "Payment refunded",
  "data": {
    "status": "REFUNDED",
    ...
  }
}
```

### GET /api/v1/payments/{paymentId}/status

**Check payment status**

Response:

```json
{
  "success": true,
  "message": "Payment status",
  "data": "APPROVED"
}
```

## Error Handling

**Global Exception Handler** catches:

- `RuntimeException` - Generic runtime errors (HTTP 400)
- `MethodArgumentNotValidException` - Validation errors (HTTP 400)
- `Exception` - Unexpected errors (HTTP 500)

Error Response:

```json
{
  "message": "Error description",
  "status": "ERROR",
  "timestamp": 1703088000000
}
```

## Testing

### Sample cURL Commands

**Process Payment (Credit Card):**

```bash
curl -X POST http://localhost:8084/api/v1/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-123",
    "customerId": "cust-456",
    "amount": 45.99,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "expiryDate": "12/25",
    "cvv": "123"
  }'
```

**Get Payment Status:**

```bash
curl http://localhost:8084/api/v1/payments/{paymentId}/status
```

**Get Customer Payments:**

```bash
curl http://localhost:8084/api/v1/payments/customer/cust-456
```

## Key Characteristics

1. **No Real Payment Gateway Integration** - Pure mock implementation
2. **H2 In-Memory Database** - No external database required
3. **UUID for Payment IDs** - Globally unique identifiers
4. **80% Success Rate** - Realistically simulates payment failure scenarios
5. **Transaction Reference Generation** - MOCK_TXN_XXXX format for traceability
6. **Timestamp Tracking** - Created and updated timestamps for audit
7. **Sensitive Data Protection** - Card numbers not stored
8. **Refund Support** - Can refund approved payments
9. **Eureka Discovery** - Service registry integration
10. **Swagger Documentation** - Full API documentation available

## Future Enhancements

- Connect to real payment gateways (Stripe, PayPal, etc.)
- Implement payment retry logic
- Add payment reconciliation reports
- Implement PCI compliance requirements
- Add multi-currency support
- Implement webhook notifications for payment events
- Add payment fraud detection
- Implement payment scheduling
