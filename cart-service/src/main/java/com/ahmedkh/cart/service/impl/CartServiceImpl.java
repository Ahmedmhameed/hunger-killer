package com.ahmedkh.cart.service.impl;

import com.ahmedkh.cart.client.MenuServiceClient;
import com.ahmedkh.cart.dto.request.CartItemRequest;
import com.ahmedkh.cart.dto.request.CartItemUpdateRequest;
import com.ahmedkh.cart.dto.response.ApiResponse;
import com.ahmedkh.cart.dto.response.CartResponse;
import com.ahmedkh.cart.dto.response.MenuItemResponse;
import com.ahmedkh.cart.entity.Cart;
import com.ahmedkh.cart.entity.CartItem;
import com.ahmedkh.cart.entity.CartStatus;
import com.ahmedkh.cart.exception.BusinessException;
import com.ahmedkh.cart.exception.ResourceNotFoundException;
import com.ahmedkh.cart.mapper.CartMapper;
import com.ahmedkh.cart.repository.CartItemRepository;
import com.ahmedkh.cart.repository.CartRepository;
import com.ahmedkh.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuServiceClient menuServiceClient;
    private final CartMapper cartMapper;

    private static final int CART_EXPIRATION_MINUTES = 30;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String customerId) {
        Cart cart = getOrCreateCart(customerId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(String customerId, CartItemRequest request) {
        Cart cart = getOrCreateCart(customerId);

        ApiResponse<MenuItemResponse> response = menuServiceClient.getItemById(request.getItemId());
        if (!response.success() || response.data() == null) {
            throw new ResourceNotFoundException("Item not found in menu service");
        }

        MenuItemResponse menuItem = response.data();
        if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
            throw new BusinessException("Item is currently unavailable");
        }

        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(menuItem.getRestaurantId())) {
            if (cart.getItems().isEmpty()) {
                cart.setRestaurantId(menuItem.getRestaurantId());
            } else {
                throw new BusinessException("Cart contains items from a different restaurant");
            }
        } else if (cart.getRestaurantId() == null) {
            cart.setRestaurantId(menuItem.getRestaurantId());
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getItemId().equals(menuItem.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setSpecialNotes(request.getSpecialNotes());
            existingItem.updateSubtotal();
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .itemId(menuItem.getId())
                    .itemName(menuItem.getName())
                    .unitPrice(menuItem.getPrice())
                    .quantity(request.getQuantity())
                    .specialNotes(request.getSpecialNotes())
                    .build();
            newItem.updateSubtotal();
            cart.getItems().add(newItem);
        }

        updateCartTotalsAndExpiry(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(String customerId, UUID cartItemId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart(customerId);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        itemToUpdate.setQuantity(request.getQuantity());
        itemToUpdate.updateSubtotal();

        updateCartTotalsAndExpiry(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String customerId, UUID cartItemId) {
        Cart cart = getOrCreateCart(customerId);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        if (cart.getItems().isEmpty()) {
            cart.setRestaurantId(null);
        }

        updateCartTotalsAndExpiry(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(String customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.getItems().clear();
        cart.setRestaurantId(null);
        updateCartTotalsAndExpiry(cart);
        cartRepository.save(cart);
    }
    
    @Override
    @Transactional
    public CartResponse checkoutCart(String customerId) {
        Cart cart = getOrCreateCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }
        cart.setStatus(CartStatus.CHECKED_OUT);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    private Cart getOrCreateCart(String customerId) {
        return cartRepository.findByCustomerIdAndDeletedFalse(customerId)
                .map(cart -> {
                    if (cart.getStatus() == CartStatus.EXPIRED || cart.getStatus() == CartStatus.CHECKED_OUT) {
                        cart.setStatus(CartStatus.ACTIVE);
                        cart.getItems().clear();
                        cart.setRestaurantId(null);
                    }
                    if (cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(LocalDateTime.now())) {
                         cart.getItems().clear();
                         cart.setRestaurantId(null);
                    }
                    cart.setExpiresAt(LocalDateTime.now().plusMinutes(CART_EXPIRATION_MINUTES));
                    return cart;
                })
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .customerId(customerId)
                            .status(CartStatus.ACTIVE)
                            .expiresAt(LocalDateTime.now().plusMinutes(CART_EXPIRATION_MINUTES))
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private void updateCartTotalsAndExpiry(Cart cart) {
        cart.recalculateTotal();
        cart.setExpiresAt(LocalDateTime.now().plusMinutes(CART_EXPIRATION_MINUTES));
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOldCarts() {
        log.info("Running scheduled task to expire old carts...");
        List<Cart> expiredCarts = cartRepository.findByExpiresAtBeforeAndStatusNotAndDeletedFalse(
                LocalDateTime.now(), CartStatus.EXPIRED);
        
        for (Cart cart : expiredCarts) {
            cart.setStatus(CartStatus.EXPIRED);
            log.debug("Expired cart ID: {}", cart.getId());
        }
        cartRepository.saveAll(expiredCarts);
    }
}
