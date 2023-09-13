package com.neoflex.deal.entity.mapper.elementmapper;

import com.neoflex.deal.dto.response.element.PassportInfo;
import com.neoflex.deal.entity.Client;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PassportMapper {

    public PassportInfo toPassportInfo(Client client) {

        PassportInfo passportInfo = new PassportInfo();

        if (Objects.nonNull(client) && Objects.nonNull(client.getPassport())) {
            passportInfo.setId(client.getPassport().getId());
            passportInfo.setSeries(client.getPassport().getSeries());
            passportInfo.setNumber(client.getPassport().getNumber());
            passportInfo.setIssueBranch(client.getPassport().getIssueBranch());
            passportInfo.setIssueDate(client.getPassport().getIssueDate());
        }
        return passportInfo;
    }
}
