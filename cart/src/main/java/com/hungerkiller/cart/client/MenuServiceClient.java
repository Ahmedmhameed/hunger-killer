package com.hungerkiller.cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class MenuServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.menu.url}")
    private String menuServiceUrl;

    public MenuServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getItem(String itemId) {
        String url = menuServiceUrl + "/api/v1/menu/items/" + itemId;
        return restTemplate.getForObject(url, Map.class);
    }
}
