package com.neoflex.gateway.client.deal;

import com.neoflex.gateway.client.BaseClient;
import org.springframework.web.client.RestTemplate;

public class DocumentClient extends BaseClient {
    public DocumentClient(RestTemplate rest) {
        super(rest);
    }
}
