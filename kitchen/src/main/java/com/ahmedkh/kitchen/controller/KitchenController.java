package com.ahmedkh.kitchen.controller;

import com.ahmedkh.kitchen.dto.request.KitchenStatusUpdateRequest;
import com.ahmedkh.kitchen.dto.response.ApiResponse;
import com.ahmedkh.kitchen.dto.response.KitchenTicketResponse;
import com.ahmedkh.kitchen.service.KitchenTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/kitchen")
@Validated
@Tag(name = "Kitchen Service", description = "APIs for kitchen order ticket management")
public class KitchenController {

    private final KitchenTicketService ticketService;

    public KitchenController(KitchenTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get kitchen ticket", description = "Retrieve kitchen ticket details by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<ApiResponse<KitchenTicketResponse>> getTicket(@PathVariable @NotNull UUID ticketId) {
        log.info("Get ticket endpoint called for ID: {}", ticketId);
        KitchenTicketResponse response = ticketService.getTicket(ticketId);
        ApiResponse<KitchenTicketResponse> apiResponse = ApiResponse.<KitchenTicketResponse>builder()
                .success(true)
                .message("Ticket retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/tickets/restaurant/{restaurantId}")
    @Operation(summary = "Get active tickets", description = "List all active kitchen tickets for a restaurant")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tickets retrieved")
    public ResponseEntity<ApiResponse<List<KitchenTicketResponse>>> getActiveTickets(
            @PathVariable @NotNull String restaurantId) {
        log.info("Get active tickets endpoint called for restaurant: {}", restaurantId);
        List<KitchenTicketResponse> response = ticketService.getActiveTicketsByRestaurant(restaurantId);
        ApiResponse<List<KitchenTicketResponse>> apiResponse = ApiResponse.<List<KitchenTicketResponse>>builder()
                .success(true)
                .message("Active tickets retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/tickets/{ticketId}/status")
    @Operation(summary = "Update ticket status", description = "Update the status of a kitchen ticket")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<ApiResponse<KitchenTicketResponse>> updateTicketStatus(
            @PathVariable @NotNull UUID ticketId,
            @Valid @RequestBody KitchenStatusUpdateRequest request) {
        log.info("Update ticket status endpoint called for ticket: {} with status: {}", ticketId, request.getStatus());
        KitchenTicketResponse response = ticketService.updateTicketStatus(ticketId, request.getStatus());
        ApiResponse<KitchenTicketResponse> apiResponse = ApiResponse.<KitchenTicketResponse>builder()
                .success(true)
                .message("Ticket status updated successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
