package com.neoflex.dossier.integration.deal;

import com.neoflex.dossier.dto.enums.ApplicationStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "deal", url = "${deal.url}", configuration = ClientConfiguration.class)
public interface DealClient {

    @PutMapping(value = "/admin/application/{applicationId}/status")
    void changeApplicationStatus(@PathVariable Long applicationId, @RequestParam ApplicationStatus status);
}
