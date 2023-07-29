package com.neoflex.conveyor.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Credit Conveyor",
                description = "Service for calculating credits", version = "1.0.0",
                contact = @Contact(
                        name = "Anna Ponomareva (Votinova)",
                        email = "anyvotinova@yandex.ru",
                        url = "https://github.com/Anna-Votinova"
                )
        )
)
public class SwaggerConfig {
}
