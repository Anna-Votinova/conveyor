package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.integration.BaseClient;
import org.springframework.web.client.RestTemplate;

public class DocumentClient extends BaseClient {
    public DocumentClient(RestTemplate rest) {
        super(rest);
    }
}
