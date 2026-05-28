package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.PaymentRequest;
import com.ahmedkh.order.feign.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", fallbackFactory = PaymentServiceClientFallbackFactory.class)
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payments/process")
    ApiResponse<PaymentResponse> processPayment(@RequestBody PaymentRequest request);
}
