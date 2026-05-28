package com.ahmedkh.order.feign.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockDto {
    private String itemId;
    private String itemName;
    private BigDecimal quantityAvailable;
    private Boolean isAvailable;
}
