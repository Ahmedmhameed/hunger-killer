package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", fallbackFactory = CartServiceClientFallbackFactory.class)
public interface CartServiceClient {

    @GetMapping("/api/v1/cart/{customerId}")
    ApiResponse<CartDto> getCart(@PathVariable String customerId);

    @PatchMapping("/api/v1/cart/{customerId}/checkout")
    ApiResponse<CartDto> checkoutCart(@PathVariable String customerId);
}
