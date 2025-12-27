package com.blackjack.infrastructure.config.swagger;

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
                        .title("Blackjack API")
                        .version("1.0")
                        .description("API REST for Blackjack game with Spring WebFlux")
                        .contact(new Contact()
                                .name("Blackjack API Team")
                                .email("contact@blackjack-api.com")));
    }
}