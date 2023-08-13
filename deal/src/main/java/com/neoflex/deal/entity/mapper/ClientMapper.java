package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientMapper {

    private final EmploymentMapper employmentMapper;

    public Client toClientShort(LoanApplicationRequestDTO requestDTO) {

        Client client = new Client();

        client.setLastName(requestDTO.getLastName());
        client.setFirstName(requestDTO.getFirstName());
        client.setMiddleName(requestDTO.getMiddleName());
        client.setBirthdate(requestDTO.getBirthdate());
        client.setEmail(requestDTO.getEmail());
        client.getPassport().setSeries(requestDTO.getPassportSeries());
        client.getPassport().setNumber(requestDTO.getPassportNumber());

        return client;

    }

    public Client toClientFull(Client client, FinishRegistrationRequestDTO requestDTO) {

        client.setGender(requestDTO.getGender());
        client.setMaritalStatus(requestDTO.getMaritalStatus());
        client.setDependentAmount(requestDTO.getDependentAmount());
        client.getPassport().setIssueDate(requestDTO.getPassportIssueDate());
        client.getPassport().setIssueBranch(requestDTO.getPassportIssueBranch());
        client.setEmployment(employmentMapper.toEmployment(requestDTO.getEmployment()));
        client.setAccount(requestDTO.getAccount());

        return client;
    }

}
