package com.neoflex.gateway.client.application;

import com.neoflex.gateway.client.BaseClient;
import org.springframework.web.client.RestTemplate;

public class ApplicationClient extends BaseClient {

    public ApplicationClient(RestTemplate rest) {
        super(rest);
    }
}
