package com.ahmedkh.order.dto.response;

import com.ahmedkh.order.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderStatusHistoryResponse {
    private UUID id;
    private OrderStatus status;
    private LocalDateTime changedAt;
    private String note;
}
