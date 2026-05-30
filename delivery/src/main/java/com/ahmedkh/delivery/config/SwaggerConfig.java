package com.ahmedkh.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Service API")
                        .version("1.0.0")
                        .description("Mock Delivery Microservice for Hunger Killer Food Ordering System")
                        .contact(new Contact()
                                .name("Ahmed Yehea")
                                .email("ahmed@example.com")));
    }
}
