package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.CartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartServiceClientFallbackFactory implements FallbackFactory<CartServiceClient> {

    @Override
    public CartServiceClient create(Throwable cause) {
        return new CartServiceClient() {
            @Override
            public ApiResponse<CartDto> getCart(String customerId) {
                log.error("CartService getCart fallback triggered: {}", cause.getMessage());
                throw new RuntimeException("Cart service unavailable: " + cause.getMessage());
            }

            @Override
            public ApiResponse<CartDto> checkoutCart(String customerId) {
                log.error("CartService checkoutCart fallback triggered: {}", cause.getMessage());
                throw new RuntimeException("Cart service unavailable: " + cause.getMessage());
            }
        };
    }
}
