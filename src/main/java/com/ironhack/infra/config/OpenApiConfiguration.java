package com.ironhack.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI medicareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediCare API")
                        .description("REST API for doctors and appointments.")
                        .version("1.0.0"));
    }
}
