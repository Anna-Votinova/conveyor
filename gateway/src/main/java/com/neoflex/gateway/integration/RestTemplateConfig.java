package com.neoflex.gateway.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplateApplication(@Value("${APPLICATION_URL}") String serverUrlApplication,
                                                RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrlApplication))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    @Bean
    public RestTemplate restTemplateDeal(@Value("${DEAL_URL}") String serverUrlDeal,
                                         RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrlDeal))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }
}
