package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.response.ApplicationDTO;
import com.neoflex.deal.dto.response.element.ApplicationStatusHistoryDTO;
import com.neoflex.deal.dto.response.element.AppliedOfferInfo;
import com.neoflex.deal.dto.response.element.ClientInfo;
import com.neoflex.deal.dto.response.element.CreditInfo;
import com.neoflex.deal.entity.Application;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationMapper {

    public ApplicationDTO toApplicationDTO(Application application, ClientInfo clientInfo, CreditInfo creditInfo,
                                           AppliedOfferInfo appliedOfferInfo,
                                           List<ApplicationStatusHistoryDTO> statusHistory) {

        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(application.getId());
        applicationDTO.setClientInfo(clientInfo);
        applicationDTO.setCreditInfo(creditInfo);
        applicationDTO.setStatus(application.getStatus());
        applicationDTO.setCreationDate(application.getCreationDate());
        applicationDTO.setAppliedOfferInfo(appliedOfferInfo);
        applicationDTO.setSignDate(application.getSignDate());
        applicationDTO.setStatusHistory(statusHistory);

        return applicationDTO;
    }
}
