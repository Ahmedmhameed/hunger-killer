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
public class KitchenStationResponse {
    private UUID id;
    private String restaurantId;
    private String name;
    private String stationType;
    private Integer currentLoad;
    private Integer maxCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
