package com.ahmedkh.payment.controller;

import com.ahmedkh.payment.dto.request.ProcessPaymentRequest;
import com.ahmedkh.payment.dto.response.ApiResponse;
import com.ahmedkh.payment.dto.response.PaymentResponse;
import com.ahmedkh.payment.entity.PaymentStatus;
import com.ahmedkh.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and management (Mock Implementation)")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Health check / Hello endpoint
     */
    @GetMapping("/")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(ApiResponse.ok("Payment Service is running", "v1.0.0"));
    }

    /**
     * Process a new payment
     */
    @PostMapping("/process")
    @Operation(summary = "Process a new payment (Mock)")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        HttpStatus status = response.getStatus() == PaymentStatus.APPROVED ? 
                HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponse.ok("Payment processed", response));
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable UUID paymentId) {
        PaymentResponse response = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved", response));
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved", response));
    }

    /**
     * Get all payments for a customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all payments for a customer")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByCustomer(
            @PathVariable String customerId) {
        List<PaymentResponse> response = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved", response));
    }

    /**
     * Refund a payment
     */
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund an approved payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable UUID paymentId) {
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Payment refunded", response));
    }

    /**
     * Check payment status
     */
    @GetMapping("/{paymentId}/status")
    @Operation(summary = "Check payment status")
    public ResponseEntity<ApiResponse<PaymentStatus>> checkStatus(
            @PathVariable UUID paymentId) {
        PaymentStatus status = paymentService.checkPaymentStatus(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Payment status", status));
    }
}
