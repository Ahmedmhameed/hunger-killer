package com.ahmedkh.kitchen.controller;

import com.ahmedkh.kitchen.dto.request.KitchenStatusUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class KitchenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUpdateTicketStatus() throws Exception {
        KitchenStatusUpdateRequest request = KitchenStatusUpdateRequest.builder()
                .status("IN_PREPARATION")
                .build();

        mockMvc.perform(patch("/api/v1/kitchen/tickets/550e8400-e29b-41d4-a716-446655440000/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetActiveTickets() throws Exception {
        mockMvc.perform(get("/api/v1/kitchen/tickets/restaurant/550e8400-e29b-41d4-a716-446655440000")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isUnauthorized());
    }
}
