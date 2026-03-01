package com.asthethi.docprocessor.swagger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
        info = @Info(
                title = "Bank Statement Analyzer",
                version = "1.0.0"
        ),
        servers = {@Server(description = "Local Server",
        url = "http://localhost:8088")})
public class OpenApiConfig {
}
