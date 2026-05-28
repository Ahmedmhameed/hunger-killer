package com.ahmedkh.order.dto.response;

import com.ahmedkh.order.entity.OrderStatus;
import com.ahmedkh.order.entity.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private String customerId;
    private String restaurantId;
    private String cartId;
    private String deliveryAddress;
    private String paymentId;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String specialInstructions;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private Integer estimatedDeliveryMinutes;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistoryResponse> statusHistory;
}
