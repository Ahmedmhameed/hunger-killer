package com.ahmedkh.payment.service;

import com.ahmedkh.payment.dto.request.ProcessPaymentRequest;
import com.ahmedkh.payment.dto.response.PaymentResponse;
import com.ahmedkh.payment.entity.Payment;
import com.ahmedkh.payment.entity.PaymentMethod;
import com.ahmedkh.payment.entity.PaymentStatus;
import com.ahmedkh.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Process a payment (Mock Implementation)
     * - Simulates payment processing without connecting to real payment gateway
     * - Randomly approves/rejects payments
     * - Stores payment data in H2 database
     */
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment for order: {} by customer: {}", request.getOrderId(), request.getCustomerId());

        // Create a new payment entity
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .build();

        // Mock payment processing - 80% success rate
        if (Math.random() < 0.8) {
            payment.setStatus(PaymentStatus.APPROVED);
            payment.setTransactionRef("MOCK_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            payment.setProcessedAt(LocalDateTime.now());
            log.info("Payment approved with transaction reference: {}", payment.getTransactionRef());
        } else {
            payment.setStatus(PaymentStatus.REJECTED);
            payment.setProcessedAt(LocalDateTime.now());
            log.warn("Payment rejected for order: {}", request.getOrderId());
        }

        // Extract last 4 digits if card payment
        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD || 
            request.getPaymentMethod() == PaymentMethod.DEBIT_CARD) {
            if (request.getCardNumber() != null && request.getCardNumber().length() >= 4) {
                payment.setCardLastFour(request.getCardNumber().substring(request.getCardNumber().length() - 4));
            }
        }

        // Save payment to database
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved with ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPayment(UUID paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return mapToResponse(payment);
    }

    /**
     * Get payment by order ID
     */
    public PaymentResponse getPaymentByOrderId(String orderId) {
        log.info("Fetching payment for order ID: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return mapToResponse(payment);
    }

    /**
     * Get all payments for a customer
     */
    public List<PaymentResponse> getPaymentsByCustomerId(String customerId) {
        log.info("Fetching payments for customer: {}", customerId);
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Refund a payment
     */
    public PaymentResponse refundPayment(UUID paymentId) {
        log.info("Processing refund for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new RuntimeException("Only approved payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());

        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully");

        return mapToResponse(refundedPayment);
    }

    /**
     * Check payment status
     */
    public PaymentStatus checkPaymentStatus(UUID paymentId) {
        log.info("Checking status for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return payment.getStatus();
    }

    /**
     * Map Payment entity to PaymentResponse DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .description(payment.getDescription())
                .processedAt(payment.getProcessedAt())
                .cardLastFour(payment.getCardLastFour())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
