package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.PaymentRequest;
import com.ahmedkh.order.feign.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceClientFallbackFactory implements FallbackFactory<PaymentServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceClientFallbackFactory.class);

    @Override
    public PaymentServiceClient create(Throwable cause) {
        return request -> {
            log.error("PaymentService processPayment fallback: {}", cause.getMessage());
            throw new RuntimeException("Payment service unavailable: " + cause.getMessage());
        };
    }
}
