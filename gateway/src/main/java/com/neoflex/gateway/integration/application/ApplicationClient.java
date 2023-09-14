package com.neoflex.gateway.integration.application;

import com.neoflex.gateway.integration.BaseClient;
import org.springframework.web.client.RestTemplate;

public class ApplicationClient extends BaseClient {

    public ApplicationClient(RestTemplate rest) {
        super(rest);
    }
}
