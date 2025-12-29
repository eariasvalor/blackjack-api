package com.blackjack.infrastructure.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@Profile("production")
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Value("${MYSQL_URL:#{null}}")
    private String mysqlUrl;

    @Value("${SPRING_R2DBC_URL:#{null}}")
    private String r2dbcUrl;

    @Override
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        String url = r2dbcUrl != null ? r2dbcUrl : convertToR2dbc(mysqlUrl);

        if (url == null) {
            throw new IllegalStateException("No database URL configured. Set SPRING_R2DBC_URL or MYSQL_URL");
        }

        return ConnectionFactories.get(url);
    }

    private String convertToR2dbc(String mysqlUrl) {
        if (mysqlUrl == null) {
            return null;
        }

        if (mysqlUrl.startsWith("mysql://")) {
            return "r2dbc:" + mysqlUrl;
        }

        if (mysqlUrl.startsWith("r2dbc:")) {
            return mysqlUrl;
        }

        throw new IllegalArgumentException("Invalid MySQL URL format: " + mysqlUrl);
    }
}