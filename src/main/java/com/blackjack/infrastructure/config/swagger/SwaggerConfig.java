package com.blackjack.infrastructure.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server; // Importar esto
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.r2dbc.url:https://blackjack-api-production-90af.up.railway.app}")
    private String productionUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        String railwayUrl = "https://blackjack-api-production-90af.up.railway.app";

        return new OpenAPI()
                .info(new Info()
                        .title("Blackjack API")
                        .version("1.0")
                        .description("API REST for Blackjack game with Spring WebFlux")
                        .contact(new Contact()
                                .name("Blackjack API Team")
                                .email("contact@blackjack-api.com")))
                .servers(List.of(new Server().url(railwayUrl).description("Production Server")));
    }
}