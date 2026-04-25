package com.hungerkiller.customer.kafka;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class CustomerRegisteredEvent {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String loyaltyTier;   // BRONZE / SILVER / GOLD
    private String registeredAt;

    public CustomerRegisteredEvent() {}

    public CustomerRegisteredEvent(String customerId, String firstName,
                                   String lastName, String email) {
        this.customerId   = customerId;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.email        = email;
        this.loyaltyTier  = "BRONZE";
        this.registeredAt = LocalDateTime.now().toString();
    }
}
