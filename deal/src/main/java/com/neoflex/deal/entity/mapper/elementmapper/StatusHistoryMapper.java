package com.neoflex.deal.entity.mapper.elementmapper;

import com.neoflex.deal.dto.response.element.ApplicationStatusHistoryDTO;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import org.springframework.stereotype.Service;

@Service
public class StatusHistoryMapper {

    public ApplicationStatusHistoryDTO toStatusHistoryDTO(StatusHistory statusHistory) {

        ApplicationStatusHistoryDTO statusHistoryDTO = new ApplicationStatusHistoryDTO();
        statusHistoryDTO.setId(statusHistory.getId());
        statusHistoryDTO.setStatus(statusHistory.getStatus());
        statusHistoryDTO.setTime(statusHistory.getTime());
        statusHistoryDTO.setChangeType(statusHistory.getChangeType());

        return statusHistoryDTO;
    }
}
