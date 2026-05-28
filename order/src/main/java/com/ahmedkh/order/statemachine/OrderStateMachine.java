package com.ahmedkh.order.statemachine;

import com.ahmedkh.order.entity.Order;
import com.ahmedkh.order.entity.OrderStatus;
import com.ahmedkh.order.entity.OrderStatusHistory;
import com.ahmedkh.order.repository.OrderRepository;
import com.ahmedkh.order.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dedicated state machine component for Order lifecycle transitions.
 * All state changes are validated here before being persisted.
 *
 * State transitions:
 *   PLACED      → CONFIRMED       (payment success — handled at placement time)
 *   CONFIRMED   → PREPARING       (kitchen status = IN_PREPARATION)
 *   PREPARING   → READY           (kitchen status = READY)
 *   READY       → DELIVERING      (delivery status = ASSIGNED or PICKED_UP)
 *   DELIVERING  → DELIVERED       (delivery status = DELIVERED)
 *   Any non-DELIVERED → CANCELED  (manual cancel or payment failure)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStateMachine {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;

    /**
     * Transition triggered by kitchen.status Kafka events.
     */
    @Transactional
    public void applyKitchenStatusTransition(String orderId, String kitchenStatus) {
        Order order = findOrder(orderId);
        if (order == null) return;

        OrderStatus newStatus = switch (kitchenStatus.toUpperCase()) {
            case "IN_PREPARATION" -> {
                if (order.getStatus() == OrderStatus.CONFIRMED) yield OrderStatus.PREPARING;
                else { log.warn("Cannot move to PREPARING from {}", order.getStatus()); yield null; }
            }
            case "READY" -> {
                if (order.getStatus() == OrderStatus.PREPARING) yield OrderStatus.READY;
                else { log.warn("Cannot move to READY from {}", order.getStatus()); yield null; }
            }
            case "CANCELED" -> {
                if (order.getStatus() != OrderStatus.DELIVERED) yield OrderStatus.CANCELED;
                else { log.warn("Cannot cancel a DELIVERED order"); yield null; }
            }
            default -> { log.warn("Unknown kitchen status: {}", kitchenStatus); yield null; }
        };

        if (newStatus != null) {
            applyTransition(order, newStatus, "Kitchen updated status to " + kitchenStatus);
        }
    }

    /**
     * Transition triggered by delivery.status Kafka events.
     */
    @Transactional
    public void applyDeliveryStatusTransition(String orderId, String deliveryStatus) {
        Order order = findOrder(orderId);
        if (order == null) return;

        OrderStatus newStatus = switch (deliveryStatus.toUpperCase()) {
            case "ASSIGNED", "PICKED_UP" -> {
                if (order.getStatus() == OrderStatus.READY) yield OrderStatus.DELIVERING;
                else { log.warn("Cannot move to DELIVERING from {}", order.getStatus()); yield null; }
            }
            case "DELIVERED" -> {
                if (order.getStatus() == OrderStatus.DELIVERING) yield OrderStatus.DELIVERED;
                else { log.warn("Cannot move to DELIVERED from {}", order.getStatus()); yield null; }
            }
            case "FAILED" -> {
                if (order.getStatus() != OrderStatus.DELIVERED) yield OrderStatus.CANCELED;
                else { log.warn("Cannot cancel a DELIVERED order"); yield null; }
            }
            default -> { log.warn("Unknown delivery status: {}", deliveryStatus); yield null; }
        };

        if (newStatus != null) {
            applyTransition(order, newStatus, "Delivery updated status to " + deliveryStatus);
        }
    }

    /**
     * Cancel an order manually (only if not yet DELIVERED).
     */
    @Transactional
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order can only be canceled when PLACED or CONFIRMED, current: " + order.getStatus());
        }

        return applyTransition(order, OrderStatus.CANCELED, reason != null ? reason : "Manual cancellation");
    }

    // -----------------------------------------------------------------------

    private Order applyTransition(Order order, OrderStatus newStatus, String note) {
        log.info("Order {} transitioning {} → {}", order.getId(), order.getStatus(), newStatus);
        order.setStatus(newStatus);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .changedAt(LocalDateTime.now())
                .note(note)
                .build();

        order.getStatusHistory().add(history);
        return orderRepository.save(order);
    }

    private Order findOrder(String orderId) {
        try {
            UUID uuid = UUID.fromString(orderId);
            return orderRepository.findByIdAndDeletedFalse(uuid).orElseGet(() -> {
                log.warn("Order not found for state transition: {}", orderId);
                return null;
            });
        } catch (IllegalArgumentException e) {
            log.error("Invalid orderId UUID format: {}", orderId);
            return null;
        }
    }
}
