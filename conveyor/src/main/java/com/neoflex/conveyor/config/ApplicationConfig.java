package com.neoflex.conveyor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {

    @NotNull
    @DecimalMin("1")
    private BigDecimal globalRate;
}
