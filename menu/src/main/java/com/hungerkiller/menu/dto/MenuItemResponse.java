package com.hungerkiller.menu.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MenuItemResponse {

    private String  itemId;
    private String  name;
    private String  description;
    private Double  price;
    private String  categoryId;
    private String  categoryName;
    private String  imageUrl;
    private Boolean isAvailable;
    private Boolean isFeatured;
}
