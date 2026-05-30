package com.ahmedkh.delivery.service;

import com.ahmedkh.delivery.dto.request.RequestDeliveryRequest;
import com.ahmedkh.delivery.dto.request.UpdateLocationRequest;
import com.ahmedkh.delivery.dto.response.DeliveryResponse;
import com.ahmedkh.delivery.entity.DeliveryStatus;

import java.util.List;
import java.util.UUID;

public interface DeliveryServiceInterface {
    DeliveryResponse requestDelivery(RequestDeliveryRequest request);
    DeliveryResponse getDelivery(UUID deliveryId);
    DeliveryResponse getDeliveryByOrderId(String orderId);
    List<DeliveryResponse> getDeliveriesByCustomerId(String customerId);
    DeliveryResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status);
    DeliveryResponse updateDriverLocation(UUID deliveryId, UpdateLocationRequest request);
    List<DeliveryResponse> getDeliveriesForDriver(String driverId);
    DeliveryResponse submitDeliveryFeedback(UUID deliveryId, String feedback, Integer rating);
    DeliveryResponse completeDelivery(UUID deliveryId, String feedback, Integer rating);
    List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status);
    DeliveryResponse cancelDelivery(UUID deliveryId, String reason);
}
