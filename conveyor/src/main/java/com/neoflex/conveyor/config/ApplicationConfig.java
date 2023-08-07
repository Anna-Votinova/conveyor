package com.neoflex.conveyor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final Environment env;

    public BigDecimal getGlobalRate() {
        return new BigDecimal(Objects.requireNonNull(env.getProperty("rate.property")));
    }
}
