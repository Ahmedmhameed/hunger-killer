package com.ahmedkh.kitchen.service;

import com.ahmedkh.kitchen.dto.response.KitchenTicketResponse;

import java.util.List;
import java.util.UUID;

public interface KitchenTicketService {
    KitchenTicketResponse getTicket(UUID ticketId);

    List<KitchenTicketResponse> getActiveTicketsByRestaurant(String restaurantId);

    KitchenTicketResponse updateTicketStatus(UUID ticketId, String status);

    void createTicketFromOrder(String orderId, String restaurantId, List<String> itemIds, List<String> itemNames, List<Integer> quantities);
}
