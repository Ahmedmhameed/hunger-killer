package com.ahmedkh.order.service.impl;

import com.ahmedkh.order.dto.request.PlaceOrderRequest;
import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.dto.response.OrderResponse;
import com.ahmedkh.order.entity.*;
import com.ahmedkh.order.exception.BusinessException;
import com.ahmedkh.order.exception.ResourceNotFoundException;
import com.ahmedkh.order.feign.CartServiceClient;
import com.ahmedkh.order.feign.InventoryServiceClient;
import com.ahmedkh.order.feign.PaymentServiceClient;
import com.ahmedkh.order.feign.dto.*;
import com.ahmedkh.order.kafka.OrderEventPublisher;
import com.ahmedkh.order.mapper.OrderMapper;
import com.ahmedkh.order.repository.OrderRepository;
import com.ahmedkh.order.repository.OrderStatusHistoryRepository;
import com.ahmedkh.order.service.OrderService;
import com.ahmedkh.order.statemachine.OrderStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final OrderMapper orderMapper;
    private final OrderStateMachine orderStateMachine;
    private final OrderEventPublisher orderEventPublisher;
    private final CartServiceClient cartServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Value("${app.order.delivery-fee:5.00}")
    private BigDecimal deliveryFee;

    @Override
    @Transactional
    public OrderResponse placeOrder(String customerId, PlaceOrderRequest request) {
        // ── STEP 1: Fetch and validate cart ──────────────────────────────────
        log.info("Placing order for customer={}, cartId={}", customerId, request.getCartId());

        ApiResponse<CartDto> cartApiResponse = cartServiceClient.getCart(customerId);
        CartDto cart = cartApiResponse.getData();

        if (cart == null) {
            throw new BusinessException("Cart not found for customer: " + customerId);
        }
        if (!"ACTIVE".equalsIgnoreCase(cart.getStatus())) {
            throw new BusinessException("Cart is not active. Status: " + cart.getStatus());
        }
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }
        if (!cart.getId().toString().equals(request.getCartId())) {
            throw new BusinessException("Cart ID mismatch");
        }

        // ── STEP 2: Snapshot cart items into OrderItem list ──────────────────
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            BigDecimal itemSubtotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            return OrderItem.builder()
                    .itemId(cartItem.getItemId())
                    .itemName(cartItem.getItemName())
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(itemSubtotal)
                    .build();
        }).collect(Collectors.toList());

        // ── STEP 3: Validate stock with Inventory Service (sync Feign) ───────
        for (CartItemDto item : cart.getItems()) {
            try {
                ApiResponse<StockDto> stockResp = inventoryServiceClient.getStockForItem(item.getItemId());
                StockDto stock = stockResp.getData();
                if (stock != null && Boolean.FALSE.equals(stock.getIsAvailable())) {
                    throw new BusinessException("Item '" + item.getItemName() + "' is currently out of stock");
                }
            } catch (BusinessException ex) {
                throw ex;
            } catch (Exception ex) {
                // Inventory service unreachable — log but don't block order (graceful degradation)
                log.warn("Could not verify stock for item {}: {}", item.getItemId(), ex.getMessage());
            }
        }

        // ── STEP 4: Calculate totals ──────────────────────────────────────────
        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grandTotal = subtotal.add(deliveryFee);

        // ── STEP 5: Process payment synchronously ─────────────────────────────
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(UUID.randomUUID().toString()) // temp ID for payment request
                .customerId(customerId)
                .amount(grandTotal)
                .paymentMethod(request.getPaymentMethod())
                .cardToken(request.getCardToken())
                .build();

        PaymentResponse paymentResponse;
        try {
            ApiResponse<PaymentResponse> paymentApiResp = paymentServiceClient.processPayment(paymentRequest);
            paymentResponse = paymentApiResp.getData();
        } catch (Exception ex) {
            throw new BusinessException("Payment processing failed: " + ex.getMessage());
        }

        if (paymentResponse == null || !"SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
            String reason = paymentResponse != null ? paymentResponse.getStatus() : "unknown";
            throw new BusinessException("Payment was declined. Reason: " + reason);
        }

        // ── STEP 6a: Persist order with CONFIRMED status ──────────────────────
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(cart.getRestaurantId())
                .cartId(cart.getId().toString())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryAddressId(request.getDeliveryAddressId())
                .paymentId(paymentResponse.getPaymentId())
                .status(OrderStatus.CONFIRMED)
                .paymentMethod(request.getPaymentMethod())
                .specialInstructions(request.getSpecialInstructions())
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .grandTotal(grandTotal)
                .estimatedDeliveryMinutes(45)
                .placedAt(now)
                .items(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .build();

        // Link items to order
        orderItems.forEach(item -> {
            item.setOrder(order);
            order.getItems().add(item);
        });

        // ── STEP 6b: Persist status history (PLACED then CONFIRMED) ──────────
        OrderStatusHistory placedEntry = OrderStatusHistory.builder()
                .order(order).status(OrderStatus.PLACED)
                .changedAt(now).note("Order placed").build();
        OrderStatusHistory confirmedEntry = OrderStatusHistory.builder()
                .order(order).status(OrderStatus.CONFIRMED)
                .changedAt(now.plusSeconds(1)).note("Payment confirmed").build();
        order.getStatusHistory().add(placedEntry);
        order.getStatusHistory().add(confirmedEntry);

        Order savedOrder = orderRepository.save(order);
        log.info("Order {} saved with status CONFIRMED", savedOrder.getId());

        // ── STEP 6c: Mark cart as CHECKED_OUT ────────────────────────────────
        try {
            cartServiceClient.checkoutCart(customerId);
        } catch (Exception ex) {
            log.error("Failed to mark cart as checked out for customer {}: {}", customerId, ex.getMessage());
            // Non-fatal: order is already committed — cart expiry will clean it up
        }

        // ── STEP 6d: Publish OrderPlacedEvent to Kafka ────────────────────────
        List<Map<String, Object>> eventItems = savedOrder.getItems().stream().map(item -> {
            Map<String, Object> m = new HashMap<>();
            m.put("itemId", item.getItemId());
            m.put("itemName", item.getItemName());
            m.put("quantity", item.getQuantity());
            m.put("unitPrice", item.getUnitPrice().doubleValue());
            return m;
        }).toList();

        orderEventPublisher.publishOrderPlaced(
                savedOrder.getId().toString(),
                customerId,
                savedOrder.getRestaurantId(),
                eventItems,
                grandTotal.doubleValue(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getSpecialInstructions()
        );

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId, String requestingCustomerId) {
        Order order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Customers can only view their own orders; admins/service roles bypass this
        if (requestingCustomerId != null
                && !order.getCustomerId().equals(requestingCustomerId)) {
            throw new BusinessException("Access denied to order: " + orderId);
        }
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForCustomer(String customerId) {
        return orderRepository
                .findByCustomerIdAndDeletedFalseOrderByPlacedAtDesc(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String requestingCustomerId) {
        Order order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getCustomerId().equals(requestingCustomerId)) {
            throw new BusinessException("Access denied to cancel order: " + orderId);
        }

        Order canceled = orderStateMachine.cancelOrder(orderId, "Customer requested cancellation");
        return orderMapper.toResponse(canceled);
    }
}
