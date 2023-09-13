package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.response.element.ClientInfo;
import com.neoflex.deal.dto.response.element.EmploymentInfo;
import com.neoflex.deal.dto.response.element.PassportInfo;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.entity.Passport;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ClientMapper {

    public Client toClientShort(LoanApplicationRequestDTO requestDTO) {

        Passport passport = new Passport();
        passport.setSeries(requestDTO.getPassportSeries());
        passport.setNumber(requestDTO.getPassportNumber());

        Client client = new Client();
        client.setPassport(passport);
        client.setLastName(requestDTO.getLastName());
        client.setFirstName(requestDTO.getFirstName());
        client.setMiddleName(requestDTO.getMiddleName());
        client.setBirthdate(requestDTO.getBirthdate());
        client.setEmail(requestDTO.getEmail());

        return client;
    }

    public Client fillAdditionalClientInfo(Client client, FinishRegistrationRequestDTO requestDTO,
                                           Employment employment) {
        client.setGender(requestDTO.getGender());
        client.setMaritalStatus(requestDTO.getMaritalStatus());
        client.setDependentAmount(requestDTO.getDependentAmount());
        client.getPassport().setIssueDate(requestDTO.getPassportIssueDate());
        client.getPassport().setIssueBranch(requestDTO.getPassportIssueBranch());
        client.setEmployment(employment);
        client.setAccount(requestDTO.getAccount());

        return client;
    }

    public ClientInfo toClientInfo(Client client, PassportInfo passportInfo, EmploymentInfo employmentInfo) {

        ClientInfo clientInfo = new ClientInfo();

        if (Objects.nonNull(client)) {
            clientInfo.setId(client.getId());
            clientInfo.setLastName(client.getLastName());
            clientInfo.setFirstName(client.getFirstName());
            clientInfo.setMiddleName(client.getMiddleName() != null ? client.getMiddleName() : "");
            clientInfo.setBirthdate(client.getBirthdate());
            clientInfo.setEmail(client.getEmail());
            clientInfo.setGender(client.getGender());
            clientInfo.setMaritalStatus(client.getMaritalStatus());
            clientInfo.setDependentAmount(client.getDependentAmount());
            clientInfo.setPassportInfo(passportInfo);
            clientInfo.setEmploymentInfo(employmentInfo);
            clientInfo.setAccount(client.getAccount());
        }
        return clientInfo;
    }
}
