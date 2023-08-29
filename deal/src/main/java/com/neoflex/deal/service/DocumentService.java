package com.neoflex.deal.service;

import com.neoflex.deal.exception.NotCompletedImplementationException;
import com.neoflex.deal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

    private final ApplicationRepository applicationRepository;

    public void sendDocument(Long applicationId) {
        throw new NotCompletedImplementationException("Вы находитесь в сервисе Document. Метод sendDocument");
    }

    public void signDocument(Long applicationId) {
        throw new NotCompletedImplementationException("Вы находитесь в сервисе Document. Метод signDocument");
    }

    public void issueCredit(Long applicationId, String sesCode) {
        throw new NotCompletedImplementationException("Вы находитесь в сервисе Document. Метод issueCredit");
    }
}
