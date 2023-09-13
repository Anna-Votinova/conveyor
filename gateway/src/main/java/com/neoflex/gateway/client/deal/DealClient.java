package com.neoflex.gateway.client.deal;

import com.neoflex.gateway.client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class DealClient extends BaseClient {

    private static final String API_PREFIX = "/admin/application";

    @Autowired
    public DealClient(@Value("${deal.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                     .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                     .build());

    }
}
