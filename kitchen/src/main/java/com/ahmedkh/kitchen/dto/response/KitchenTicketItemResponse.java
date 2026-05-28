package com.ahmedkh.kitchen.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenTicketItemResponse {
    private UUID id;
    private String itemId;
    private String itemName;
    private Integer quantity;
    private String station;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
