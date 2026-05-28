package com.ahmedkh.order.dto.request;

import com.ahmedkh.order.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "Cart ID is required")
    private String cartId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String deliveryAddress;
    private String deliveryAddressId;
    private String specialInstructions;

    // Card token for CREDIT_CARD / MOBILE_WALLET payments
    private String cardToken;
}
