package com.neoflex.deal.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Service Deal",
                description = "Service for storing info about issued loans", version = "1.0.0",
                contact = @Contact(
                        name = "Anna Ponomareva (Votinova)",
                        email = "anyvotinova@yandex.ru",
                        url = "https://github.com/Anna-Votinova"
                )
        )
)
public class SwaggerConfig {
}
