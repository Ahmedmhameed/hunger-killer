package com.ahmedkh.delivery.service;

import com.ahmedkh.delivery.dto.request.RequestDeliveryRequest;
import com.ahmedkh.delivery.dto.request.UpdateLocationRequest;
import com.ahmedkh.delivery.dto.response.DeliveryResponse;
import com.ahmedkh.delivery.entity.Delivery;
import com.ahmedkh.delivery.entity.DeliveryStatus;
import com.ahmedkh.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    /**
     * Request a delivery (Mock Implementation)
     * - Simulates delivery assignment without connecting to real delivery systems
     * - Assigns a mock driver
     * - Sets estimated delivery time
     * - Stores delivery record in H2 database
     */
    public DeliveryResponse requestDelivery(RequestDeliveryRequest request) {
        log.info("Requesting delivery for order: {}", request.getOrderId());

        // Create a new delivery entity
        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLatitude(request.getDeliveryLatitude())
                .deliveryLongitude(request.getDeliveryLongitude())
                .deliveryNotes(request.getDeliveryNotes())
                .build();

        // Mock driver assignment - random drivers
        String[] mockDriverNames = {"Ahmed Driver", "Mohammed Delivery", "Hassan Rider", "Karim Express"};
        String[] mockDriverIds = {"drv-001", "drv-002", "drv-003", "drv-004"};
        String[] mockVehicles = {"CAR-101", "CAR-202", "BIKE-303", "CAR-404"};

        int randomIndex = (int) (Math.random() * mockDriverNames.length);
        delivery.setDriverId(mockDriverIds[randomIndex]);
        delivery.setDriverName(mockDriverNames[randomIndex]);
        delivery.setDriverPhone("+972-59-" + String.format("%07d", (int)(Math.random() * 10000000)));
        delivery.setVehicleNumber(mockVehicles[randomIndex]);

        // Mock current location (restaurant location)
        delivery.setCurrentLatitude(31.9454);
        delivery.setCurrentLongitude(35.2338);

        // Set estimated delivery time (30-45 minutes from now)
        int estimatedMinutes = 30 + (int) (Math.random() * 15);
        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(estimatedMinutes));

        // Set initial status
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        // Save delivery to database
        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery created with ID: {} for order: {}", savedDelivery.getId(), request.getOrderId());

        return mapToResponse(savedDelivery);
    }

    /**
     * Get delivery by ID
     */
    public DeliveryResponse getDelivery(UUID deliveryId) {
        log.info("Fetching delivery with ID: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));
        return mapToResponse(delivery);
    }

    /**
     * Get delivery by order ID
     */
    public DeliveryResponse getDeliveryByOrderId(String orderId) {
        log.info("Fetching delivery for order ID: {}", orderId);
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
        return mapToResponse(delivery);
    }

    /**
     * Get all deliveries for a customer
     */
    public List<DeliveryResponse> getDeliveriesByCustomerId(String customerId) {
        log.info("Fetching deliveries for customer: {}", customerId);
        List<Delivery> deliveries = deliveryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return deliveries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update delivery status
     */
    public DeliveryResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status) {
        log.info("Updating delivery {} status to {}", deliveryId, status);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(status);

        // If delivery is completed, set actual delivery time
        if (status == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryTime(LocalDateTime.now());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery status updated successfully");

        return mapToResponse(updatedDelivery);
    }

    /**
     * Update driver's current location
     */
    public DeliveryResponse updateDriverLocation(UUID deliveryId, UpdateLocationRequest request) {
        log.info("Updating driver location for delivery: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));

        delivery.setCurrentLatitude(request.getLatitude());
        delivery.setCurrentLongitude(request.getLongitude());

        // Automatically transition to IN_TRANSIT if assigned
        if (delivery.getStatus() == DeliveryStatus.ASSIGNED) {
            delivery.setStatus(DeliveryStatus.PICKED_UP);
        } else if (delivery.getStatus() == DeliveryStatus.PICKED_UP) {
            delivery.setStatus(DeliveryStatus.IN_TRANSIT);
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Driver location updated for delivery: {}", deliveryId);

        return mapToResponse(updatedDelivery);
    }

    /**
     * Complete delivery with feedback
     */
    public DeliveryResponse completeDelivery(UUID deliveryId, String feedback, Integer rating) {
        log.info("Completing delivery: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setActualDeliveryTime(LocalDateTime.now());
        delivery.setCustomerFeedback(feedback);
        delivery.setRating(rating);

        Delivery completedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery completed with ID: {}", deliveryId);

        return mapToResponse(completedDelivery);
    }

    /**
     * Cancel delivery
     */
    public DeliveryResponse cancelDelivery(UUID deliveryId, String cancellationReason) {
        log.info("Cancelling delivery: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setDeliveryNotes(cancellationReason);

        Delivery cancelledDelivery = deliveryRepository.save(delivery);
        log.info("Delivery cancelled with ID: {}", deliveryId);

        return mapToResponse(cancelledDelivery);
    }

    /**
     * Get all deliveries by status
     */
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        log.info("Fetching deliveries with status: {}", status);
        List<Delivery> deliveries = deliveryRepository.findByStatus(status);
        return deliveries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Delivery entity to DeliveryResponse DTO
     */
    private DeliveryResponse mapToResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .customerId(delivery.getCustomerId())
                .deliveryAddress(delivery.getDeliveryAddress())
                .status(delivery.getStatus())
                .driverId(delivery.getDriverId())
                .driverName(delivery.getDriverName())
                .driverPhone(delivery.getDriverPhone())
                .vehicleNumber(delivery.getVehicleNumber())
                .currentLatitude(delivery.getCurrentLatitude())
                .currentLongitude(delivery.getCurrentLongitude())
                .deliveryLatitude(delivery.getDeliveryLatitude())
                .deliveryLongitude(delivery.getDeliveryLongitude())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .deliveryNotes(delivery.getDeliveryNotes())
                .customerFeedback(delivery.getCustomerFeedback())
                .rating(delivery.getRating())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
