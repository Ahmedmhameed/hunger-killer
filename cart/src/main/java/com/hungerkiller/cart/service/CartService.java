package com.hungerkiller.cart.service;

import com.hungerkiller.cart.client.MenuServiceClient;
import com.hungerkiller.cart.dto.*;
import com.hungerkiller.cart.model.Cart;
import com.hungerkiller.cart.model.CartItem;
import com.hungerkiller.cart.repository.CartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository   cartRepository;
    private final MenuServiceClient menuClient;

    public CartService(CartRepository cartRepository,
                       MenuServiceClient menuClient) {
        this.cartRepository = cartRepository;
        this.menuClient     = menuClient;
    }

    public CartResponse addItem(String customerId, AddItemRequest req) {

        // Step 1: Call Menu MS synchronously to validate item
        Map<String, Object> menuItem;
        try {
            menuItem = menuClient.getItem(req.getItemId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item not found in menu: " + req.getItemId());
        }

        // Step 2: Check availability
        Boolean isAvailable = (Boolean) menuItem.get("isAvailable");
        if (Boolean.FALSE.equals(isAvailable)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item is currently unavailable: " + req.getItemId());
        }

        // Step 3: Find or create cart for this customer
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    return newCart;
                });

        // Step 4: Build CartItem with price snapshot from Menu MS
        CartItem cartItem = new CartItem();
        cartItem.setItemId(req.getItemId());
        cartItem.setItemName((String) menuItem.get("name"));
        cartItem.setUnitPrice(
                Double.valueOf(menuItem.get("price").toString()));
        cartItem.setQuantity(req.getQuantity());
        cartItem.setSpecialNotes(req.getSpecialNotes());
        cartItem.setCart(cart);

        cart.getItems().add(cartItem);
        Cart saved = cartRepository.save(cart);

        return toResponse(saved);
    }

    public CartResponse getCart(String customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No active cart for customer: " + customerId));
        return toResponse(cart);
    }

    public void removeItem(String customerId, String cartItemId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart not found"));

        cart.getItems().removeIf(
                i -> i.getCartItemId().equals(cartItemId));
        cartRepository.save(cart);
    }

    public void clearCart(String customerId) {
        cartRepository.deleteByCustomerId(customerId);
    }

    // ── Mapper ────────────────────────────────────────────────────────
    private CartResponse toResponse(Cart cart) {
        CartResponse resp = new CartResponse();
        resp.setCartId(cart.getCartId());
        resp.setCustomerId(cart.getCustomerId());
        resp.setRestaurantId(cart.getRestaurantId());
        resp.setTotalAmount(cart.getTotalAmount());
        resp.setExpiresAt(cart.getExpiresAt());

        List<CartItemResponse> itemDtos = cart.getItems().stream()
                .map(i -> {
                    CartItemResponse d = new CartItemResponse();
                    d.setCartItemId(i.getCartItemId());
                    d.setItemId(i.getItemId());
                    d.setItemName(i.getItemName());
                    d.setUnitPrice(i.getUnitPrice());
                    d.setQuantity(i.getQuantity());
                    d.setSubtotal(i.getSubtotal());
                    d.setSpecialNotes(i.getSpecialNotes());
                    return d;
                })
                .collect(Collectors.toList());

        resp.setItems(itemDtos);
        return resp;
    }
}
