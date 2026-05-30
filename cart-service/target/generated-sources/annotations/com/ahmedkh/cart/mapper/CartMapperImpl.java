package com.ahmedkh.cart.mapper;

import com.ahmedkh.cart.dto.response.CartItemResponse;
import com.ahmedkh.cart.dto.response.CartResponse;
import com.ahmedkh.cart.entity.Cart;
import com.ahmedkh.cart.entity.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T15:41:35+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse cartResponse = new CartResponse();

        cartResponse.setCustomerId( cart.getCustomerId() );
        cartResponse.setExpiresAt( cart.getExpiresAt() );
        cartResponse.setId( cart.getId() );
        cartResponse.setItems( cartItemListToCartItemResponseList( cart.getItems() ) );
        cartResponse.setRestaurantId( cart.getRestaurantId() );
        cartResponse.setStatus( cart.getStatus() );
        cartResponse.setTotalAmount( cart.getTotalAmount() );

        return cartResponse;
    }

    @Override
    public CartItemResponse toResponse(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemResponse cartItemResponse = new CartItemResponse();

        cartItemResponse.setId( cartItem.getId() );
        cartItemResponse.setItemId( cartItem.getItemId() );
        cartItemResponse.setItemName( cartItem.getItemName() );
        cartItemResponse.setQuantity( cartItem.getQuantity() );
        cartItemResponse.setSpecialNotes( cartItem.getSpecialNotes() );
        cartItemResponse.setSubtotal( cartItem.getSubtotal() );
        cartItemResponse.setUnitPrice( cartItem.getUnitPrice() );

        return cartItemResponse;
    }

    protected List<CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toResponse( cartItem ) );
        }

        return list1;
    }
}
