package com.ahmedkh.order.feign.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String status;   // SUCCESS | FAILED | PENDING
    private String transactionRef;
}
