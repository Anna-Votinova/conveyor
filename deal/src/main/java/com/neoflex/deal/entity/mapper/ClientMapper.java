package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.jsonb.element.Employment;
import com.neoflex.deal.entity.jsonb.element.Passport;
import com.neoflex.deal.repository.EmploymentRepository;
import com.neoflex.deal.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientMapper {

    private final EmploymentMapper employmentMapper;

    public final PassportRepository passportRepository;

    private final EmploymentRepository employmentRepository;

    public Client toClientShort(LoanApplicationRequestDTO requestDTO) {

        Passport passport = new Passport();
        passport.setSeries(requestDTO.getPassportSeries());
        passport.setNumber(requestDTO.getPassportNumber());

        Client client = new Client();

        client.setLastName(requestDTO.getLastName());
        client.setFirstName(requestDTO.getFirstName());
        client.setMiddleName(requestDTO.getMiddleName());
        client.setBirthdate(requestDTO.getBirthdate());
        client.setEmail(requestDTO.getEmail());
        client.setPassport(passportRepository.save(passport));
        return client;

    }

    public Client toClientFull(Client client, FinishRegistrationRequestDTO requestDTO) {

        Employment employment = employmentMapper.toEmployment(requestDTO.getEmployment());

        client.setGender(requestDTO.getGender());
        client.setMaritalStatus(requestDTO.getMaritalStatus());
        client.setDependentAmount(requestDTO.getDependentAmount());
        client.getPassport().setIssueDate(requestDTO.getPassportIssueDate());
        client.getPassport().setIssueBranch(requestDTO.getPassportIssueBranch());
        client.setEmployment(employmentRepository.save(employment));
        client.setAccount(requestDTO.getAccount());

        return client;
    }

}
