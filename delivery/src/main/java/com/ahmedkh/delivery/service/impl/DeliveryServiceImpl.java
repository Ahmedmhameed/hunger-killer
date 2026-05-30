package com.ahmedkh.delivery.service.impl;

import com.ahmedkh.delivery.dto.request.RequestDeliveryRequest;
import com.ahmedkh.delivery.dto.request.UpdateLocationRequest;
import com.ahmedkh.delivery.dto.response.DeliveryResponse;
import com.ahmedkh.delivery.entity.Delivery;
import com.ahmedkh.delivery.entity.DeliveryStatus;
import com.ahmedkh.delivery.exception.ResourceNotFoundException;
import com.ahmedkh.delivery.kafka.DeliveryEventProducer;
import com.ahmedkh.delivery.repository.DeliveryRepository;
import com.ahmedkh.delivery.service.DeliveryServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryServiceInterface {

    private final DeliveryRepository deliveryRepository;

    // Mock driver data
    private static final String[] DRIVER_NAMES = {"Ahmed Driver", "Mohammed Delivery", "Hassan Rider", "Karim Express", "Fatima Courier"};
    private static final String[] DRIVER_IDS = {"drv-001", "drv-002", "drv-003", "drv-004", "drv-005"};
    private static final String[] VEHICLE_NUMBERS = {"CAR-101", "CAR-202", "BIKE-303", "CAR-404", "VAN-505"};

    /**
     * Request a delivery (Mock Implementation)
     * - Simulates delivery assignment without connecting to real delivery systems
     * - Assigns a mock driver
     * - Sets estimated delivery time
     * - Stores delivery record in H2 database
     */
    @Override
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

        // Mock driver assignment - assign random driver
        int randomIndex = (int) (Math.random() * DRIVER_NAMES.length);
        delivery.setDriverId(DRIVER_IDS[randomIndex]);
        delivery.setDriverName(DRIVER_NAMES[randomIndex]);
        delivery.setDriverPhone("+972-59-" + String.format("%07d", (int)(Math.random() * 10000000)));
        delivery.setVehicleNumber(VEHICLE_NUMBERS[randomIndex]);

        // Mock current location (restaurant location - Gaza City)
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
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(UUID deliveryId) {
        log.info("Fetching delivery with ID: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));
        return mapToResponse(delivery);
    }

    /**
     * Get delivery by order ID
     */
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrderId(String orderId) {
        log.info("Fetching delivery for order ID: {}", orderId);
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
        return mapToResponse(delivery);
    }

    /**
     * Get all deliveries for a customer
     */
    @Override
    @Transactional(readOnly = true)
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
    @Override
    public DeliveryResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status) {
        log.info("Updating delivery {} status to: {}", deliveryId, status);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(status);

        // Update timestamps based on status
        if (status == DeliveryStatus.IN_TRANSIT) {
            // Simulate driver picking up order
            delivery.setCurrentLatitude(31.9454);
            delivery.setCurrentLongitude(35.2338);
        } else if (status == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryTime(LocalDateTime.now());
            delivery.setCurrentLatitude(delivery.getDeliveryLatitude());
            delivery.setCurrentLongitude(delivery.getDeliveryLongitude());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery status updated successfully");

        return mapToResponse(updatedDelivery);
    }

    /**
     * Update driver's current location (Mock GPS tracking)
     */
    @Override
    public DeliveryResponse updateDriverLocation(UUID deliveryId, UpdateLocationRequest request) {
        log.info("Updating driver location for delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setCurrentLatitude(request.getLatitude());
        delivery.setCurrentLongitude(request.getLongitude());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Driver location updated successfully");

        return mapToResponse(updatedDelivery);
    }

    /**
     * Get all deliveries for a specific driver (mock)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesForDriver(String driverId) {
        log.info("Fetching deliveries for driver: {}", driverId);
        List<Delivery> deliveries = deliveryRepository.findByDriverId(driverId);
        return deliveries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Submit delivery feedback and rating
     */
    @Override
    public DeliveryResponse submitDeliveryFeedback(UUID deliveryId, String feedback, Integer rating) {
        log.info("Submitting feedback for delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setCustomerFeedback(feedback);
        delivery.setRating(rating);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery feedback submitted successfully");

        return mapToResponse(updatedDelivery);
    }

    /**
     * Get deliveries by status (for monitoring)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        log.info("Fetching deliveries with status: {}", status);
        List<Delivery> deliveries = deliveryRepository.findByStatus(status);
        return deliveries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel delivery
     */
    @Override
    public DeliveryResponse cancelDelivery(UUID deliveryId, String reason) {
        log.info("Canceling delivery: {} with reason: {}", deliveryId, reason);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(DeliveryStatus.CANCELED);
        delivery.setDeliveryNotes(reason);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery canceled successfully");

        return mapToResponse(updatedDelivery);
    }

    /**
     * Complete delivery with feedback and rating
     */
    @Override
    public DeliveryResponse completeDelivery(UUID deliveryId, String feedback, Integer rating) {
        log.info("Completing delivery: {} with feedback and rating", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setActualDeliveryTime(LocalDateTime.now());
        if (feedback != null) {
            delivery.setCustomerFeedback(feedback);
        }
        if (rating != null) {
            delivery.setRating(rating);
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery completed successfully");

        return mapToResponse(updatedDelivery);
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
