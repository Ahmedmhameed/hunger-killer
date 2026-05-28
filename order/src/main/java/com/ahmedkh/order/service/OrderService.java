package com.ahmedkh.order.service;

import com.ahmedkh.order.dto.request.PlaceOrderRequest;
import com.ahmedkh.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse placeOrder(String customerId, PlaceOrderRequest request);
    OrderResponse getOrder(UUID orderId, String requestingCustomerId);
    List<OrderResponse> getOrdersForCustomer(String customerId);
    OrderResponse cancelOrder(UUID orderId, String requestingCustomerId);
}
