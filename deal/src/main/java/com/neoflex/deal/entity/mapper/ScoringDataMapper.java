package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.response.ScoringDataDTO;
import org.springframework.stereotype.Service;

@Service
public class ScoringDataMapper {

    public ScoringDataDTO toScoringDataDTO(Application application, FinishRegistrationRequestDTO requestDTO) {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        scoringDataDTO.setAmount(application.getAppliedOffer().getRequestedAmount());
        scoringDataDTO.setTerm(application.getAppliedOffer().getTerm());
        scoringDataDTO.setFirstName(application.getClient().getFirstName());
        scoringDataDTO.setLastName(application.getClient().getLastName());
        scoringDataDTO.setMiddleName(application.getClient().getMiddleName());
        scoringDataDTO.setGender(requestDTO.getGender());
        scoringDataDTO.setBirthdate(application.getClient().getBirthdate());
        scoringDataDTO.setPassportSeries(application.getClient().getPassport().getSeries());
        scoringDataDTO.setPassportNumber(application.getClient().getPassport().getNumber());
        scoringDataDTO.setPassportIssueDate(requestDTO.getPassportIssueDate());
        scoringDataDTO.setPassportIssueBranch(requestDTO.getPassportIssueBranch());
        scoringDataDTO.setMaritalStatus(requestDTO.getMaritalStatus());
        scoringDataDTO.setDependentAmount(requestDTO.getDependentAmount());
        scoringDataDTO.setEmployment(requestDTO.getEmployment());
        scoringDataDTO.setAccount(requestDTO.getAccount());
        scoringDataDTO.setIsInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled());
        scoringDataDTO.setIsSalaryClient(application.getAppliedOffer().getIsSalaryClient());

        return scoringDataDTO;
    }
}
