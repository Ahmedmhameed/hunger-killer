package com.ahmedkh.delivery.controller;

import com.ahmedkh.delivery.dto.request.DeliveryFeedbackRequest;
import com.ahmedkh.delivery.dto.request.RequestDeliveryRequest;
import com.ahmedkh.delivery.dto.request.UpdateLocationRequest;
import com.ahmedkh.delivery.dto.response.ApiResponse;
import com.ahmedkh.delivery.dto.response.DeliveryResponse;
import com.ahmedkh.delivery.entity.DeliveryStatus;
import com.ahmedkh.delivery.service.DeliveryServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Delivery tracking and management (Mock Implementation)")
public class DeliveryController {

    private final DeliveryServiceInterface deliveryService;

    /**
     * Health check / Hello endpoint
     */
    @GetMapping("/")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery Service is running", "v1.0.0"));
    }

    /**
     * Request a new delivery
     */
    @PostMapping("/request")
    @Operation(summary = "Request a new delivery (Mock)")
    public ResponseEntity<ApiResponse<DeliveryResponse>> requestDelivery(
            @Valid @RequestBody RequestDeliveryRequest request) {
        DeliveryResponse response = deliveryService.requestDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Delivery requested successfully", response));
    }

    /**
     * Get delivery by ID
     */
    @GetMapping("/{deliveryId}")
    @Operation(summary = "Get delivery details by ID")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDelivery(
            @PathVariable UUID deliveryId) {
        DeliveryResponse response = deliveryService.getDelivery(deliveryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery retrieved", response));
    }

    /**
     * Get delivery by order ID
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get delivery by order ID")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDeliveryByOrderId(
            @PathVariable String orderId) {
        DeliveryResponse response = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery retrieved", response));
    }

    /**
     * Get all deliveries for a customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all deliveries for a customer")
    public ResponseEntity<ApiResponse<List<DeliveryResponse>>> getDeliveriesByCustomer(
            @PathVariable String customerId) {
        List<DeliveryResponse> response = deliveryService.getDeliveriesByCustomerId(customerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deliveries retrieved", response));
    }

    /**
     * Update delivery status
     */
    @PatchMapping("/{deliveryId}/status")
    @Operation(summary = "Update delivery status")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateDeliveryStatus(
            @PathVariable UUID deliveryId,
            @RequestParam DeliveryStatus status) {
        DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery status updated", response));
    }

    /**
     * Update driver's current location
     */
    @PatchMapping("/{deliveryId}/location")
    @Operation(summary = "Update driver's current location")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateDriverLocation(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateLocationRequest request) {
        DeliveryResponse response = deliveryService.updateDriverLocation(deliveryId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver location updated", response));
    }

    /**
     * Complete delivery with feedback
     */
    @PostMapping("/{deliveryId}/complete")
    @Operation(summary = "Complete delivery with customer feedback")
    public ResponseEntity<ApiResponse<DeliveryResponse>> completeDelivery(
            @PathVariable UUID deliveryId,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) Integer rating) {
        DeliveryResponse response = deliveryService.completeDelivery(deliveryId, feedback, rating);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery completed", response));
    }

    /**
     * Cancel delivery
     */
    @PostMapping("/{deliveryId}/cancel")
    @Operation(summary = "Cancel a delivery")
    public ResponseEntity<ApiResponse<DeliveryResponse>> cancelDelivery(
            @PathVariable UUID deliveryId,
            @RequestParam(required = false) String reason) {
        DeliveryResponse response = deliveryService.cancelDelivery(deliveryId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery cancelled", response));
    }

    /**
     * Get deliveries by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get all deliveries by status")
    public ResponseEntity<ApiResponse<List<DeliveryResponse>>> getDeliveriesByStatus(
            @PathVariable DeliveryStatus status) {
        List<DeliveryResponse> response = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deliveries retrieved", response));
    }
}
