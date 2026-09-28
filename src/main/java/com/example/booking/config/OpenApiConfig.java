package com.example.booking.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI bookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resource Booking API")
                        .version("1.0.0")
                        .description("Secure resource booking REST API with JWT and RBAC"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")));
    }
}
