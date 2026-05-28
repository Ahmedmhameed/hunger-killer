package com.ahmedkh.kitchen.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenTicketResponse {
    private UUID id;
    private String orderId;
    private String restaurantId;
    private String status;
    private String specialInstructions;
    private LocalDateTime estimatedReadyAt;
    private LocalDateTime actualReadyAt;
    private Set<KitchenTicketItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
