package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.response.DocumentDTO;
import com.neoflex.deal.entity.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentMapper {

    public DocumentDTO toDocumentDto(Application application) {
        return new DocumentDTO(
                application.getClient().getFirstName(),
                application.getClient().getLastName(),
                application.getClient().getMiddleName(),
                application.getCredit().getAmount(),
                application.getCredit().getTerm(),
                application.getCredit().getMonthlyPayment(),
                application.getCredit().getRate(),
                application.getCredit().getPsk(),
                application.getCredit().getInsuranceEnable(),
                application.getCredit().getSalaryClient(),
                application.getCredit().getPaymentSchedule()
        );
    }
}
