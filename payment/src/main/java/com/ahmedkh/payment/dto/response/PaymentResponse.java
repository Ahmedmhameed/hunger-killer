package com.ahmedkh.payment.dto.response;

import com.ahmedkh.payment.entity.PaymentMethod;
import com.ahmedkh.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;

    private String orderId;

    private String customerId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionRef;

    private String description;

    private LocalDateTime processedAt;

    private String cardLastFour;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
