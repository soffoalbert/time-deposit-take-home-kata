package org.ikigaidigital.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Time Deposit API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI timeDepositOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XA Bank Time Deposit API")
                        .description("REST API for managing time deposit accounts and calculating interest")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("XA Bank Development Team")
                                .email("dev@xabank.com")
                                .url("https://xabank.com"))
                        .license(new License()
                                .name("XBank Proprietary License")
                                .url("https://xbank.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server"),
                        new Server()
                                .url("https://api.xabank.com")
                                .description("Production server")
                ));
    }
}

