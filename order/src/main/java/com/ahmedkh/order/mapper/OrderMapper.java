package com.ahmedkh.order.mapper;

import com.ahmedkh.order.dto.response.OrderItemResponse;
import com.ahmedkh.order.dto.response.OrderResponse;
import com.ahmedkh.order.dto.response.OrderStatusHistoryResponse;
import com.ahmedkh.order.entity.Order;
import com.ahmedkh.order.entity.OrderItem;
import com.ahmedkh.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    OrderItemResponse toResponse(OrderItem item);
    OrderStatusHistoryResponse toResponse(OrderStatusHistory history);
}
