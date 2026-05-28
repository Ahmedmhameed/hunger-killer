package com.ahmedkh.kitchen.service.impl;

import com.ahmedkh.kitchen.dto.response.KitchenTicketResponse;
import com.ahmedkh.kitchen.entity.*;
import com.ahmedkh.kitchen.exception.BusinessException;
import com.ahmedkh.kitchen.exception.ResourceNotFoundException;
import com.ahmedkh.kitchen.kafka.KitchenEventProducer;
import com.ahmedkh.kitchen.mapper.KitchenMapper;
import com.ahmedkh.kitchen.repository.KitchenStationRepository;
import com.ahmedkh.kitchen.repository.KitchenTicketRepository;
import com.ahmedkh.kitchen.service.KitchenTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class KitchenTicketServiceImpl implements KitchenTicketService {

    private final KitchenTicketRepository ticketRepository;
    private final KitchenStationRepository stationRepository;
    private final KitchenMapper kitchenMapper;
    private final KitchenEventProducer eventProducer;

    public KitchenTicketServiceImpl(KitchenTicketRepository ticketRepository,
                                   KitchenStationRepository stationRepository,
                                   KitchenMapper kitchenMapper,
                                   KitchenEventProducer eventProducer) {
        this.ticketRepository = ticketRepository;
        this.stationRepository = stationRepository;
        this.kitchenMapper = kitchenMapper;
        this.eventProducer = eventProducer;
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenTicketResponse getTicket(UUID ticketId) {
        log.debug("Fetching kitchen ticket with ID: {}", ticketId);

        KitchenTicket ticket = ticketRepository.findByIdAndDeletedFalse(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitchen ticket not found with ID: " + ticketId));

        return kitchenMapper.toTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenTicketResponse> getActiveTicketsByRestaurant(String restaurantId) {
        log.debug("Fetching active tickets for restaurant: {}", restaurantId);

        return ticketRepository.findActiveTicketsByRestaurant(restaurantId)
                .stream()
                .map(kitchenMapper::toTicketResponse)
                .toList();
    }

    @Override
    public KitchenTicketResponse updateTicketStatus(UUID ticketId, String status) {
        log.info("Updating ticket status for ticket: {} to status: {}", ticketId, status);

        KitchenTicket ticket = ticketRepository.findByIdAndDeletedFalse(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitchen ticket not found with ID: " + ticketId));

        try {
            KitchenStatus newStatus = KitchenStatus.valueOf(status.toUpperCase());
            
            if (newStatus == KitchenStatus.READY) {
                ticket.setActualReadyAt(LocalDateTime.now());
            }
            
            ticket.setStatus(newStatus);
            KitchenTicket updated = ticketRepository.save(ticket);

            // Publish event
            eventProducer.publishKitchenStatusEvent(updated);

            log.info("Ticket status updated successfully: {}", ticketId);
            return kitchenMapper.toTicketResponse(updated);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid status: " + status);
        }
    }

    @Override
    public void createTicketFromOrder(String orderId, String restaurantId, List<String> itemIds, 
                                      List<String> itemNames, List<Integer> quantities) {
        log.info("Creating kitchen ticket for order: {} in restaurant: {}", orderId, restaurantId);

        if (ticketRepository.findByOrderIdAndDeletedFalse(orderId).isPresent()) {
            throw new BusinessException("Kitchen ticket already exists for order: " + orderId);
        }

        KitchenTicket ticket = KitchenTicket.builder()
                .orderId(orderId)
                .restaurantId(restaurantId)
                .status(KitchenStatus.NEW)
                .estimatedReadyAt(LocalDateTime.now().plusMinutes(30))
                .build();

        // Assign items to stations
        for (int i = 0; i < itemIds.size(); i++) {
            String itemId = itemIds.get(i);
            String itemName = itemNames.get(i);
            Integer quantity = quantities.get(i);

            StationType station = determineStation(itemName);
            
            KitchenTicketItem item = KitchenTicketItem.builder()
                    .itemId(itemId)
                    .itemName(itemName)
                    .quantity(quantity)
                    .station(station)
                    .build();
            
            ticket.addItem(item);
        }

        KitchenTicket saved = ticketRepository.save(ticket);
        log.info("Kitchen ticket created successfully: {}", saved.getId());

        // Publish event
        eventProducer.publishKitchenStatusEvent(saved);
    }

    private StationType determineStation(String itemName) {
        String lowerName = itemName.toLowerCase();
        
        if (lowerName.contains("grill") || lowerName.contains("steak") || lowerName.contains("burger")) {
            return StationType.GRILL;
        } else if (lowerName.contains("salad") || lowerName.contains("cold")) {
            return StationType.COLD_PREP;
        } else if (lowerName.contains("fry") || lowerName.contains("fried")) {
            return StationType.FRYER;
        } else if (lowerName.contains("cake") || lowerName.contains("pastry") || lowerName.contains("bread")) {
            return StationType.PASTRY;
        } else if (lowerName.contains("juice") || lowerName.contains("drink") || lowerName.contains("coffee")) {
            return StationType.DRINKS;
        }
        
        return StationType.COLD_PREP;
    }
}
