package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request_responce.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.entity.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

    public Client toClientFull(Client client, FinishRegistrationRequestDTO requestDTO, Employment employment) {

        client.setGender(requestDTO.getGender());
        client.setMaritalStatus(requestDTO.getMaritalStatus());
        client.setDependentAmount(requestDTO.getDependentAmount());
        client.getPassport().setIssueDate(requestDTO.getPassportIssueDate());
        client.getPassport().setIssueBranch(requestDTO.getPassportIssueBranch());
        client.setEmployment(employment);
        client.setAccount(requestDTO.getAccount());

        return client;
    }

}
