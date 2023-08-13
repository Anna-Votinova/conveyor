package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.dto.ApplicationStatusHistoryDTO;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import org.springframework.stereotype.Service;

@Service
public class StatusHistoryMapper {

    public StatusHistory toStatusHistory(ApplicationStatusHistoryDTO historyDTO) {
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setStatus(historyDTO.getStatus());
        statusHistory.setChangeType(historyDTO.getChangeType());
        return statusHistory;
    }
}
