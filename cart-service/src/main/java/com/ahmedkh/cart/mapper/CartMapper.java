package com.ahmedkh.cart.mapper;

import com.ahmedkh.cart.dto.response.CartItemResponse;
import com.ahmedkh.cart.dto.response.CartResponse;
import com.ahmedkh.cart.entity.Cart;
import com.ahmedkh.cart.entity.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponse toResponse(Cart cart);

    CartItemResponse toResponse(CartItem cartItem);
}
