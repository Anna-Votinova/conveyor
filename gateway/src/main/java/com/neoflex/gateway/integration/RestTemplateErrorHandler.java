package com.neoflex.gateway.integration;

import com.neoflex.gateway.exception.ApplicationNotFoundException;
import com.neoflex.gateway.exception.BadRequestException;
import com.neoflex.gateway.exception.InvalidSesCodeException;
import com.neoflex.gateway.exception.ScoringConveyorException;
import com.neoflex.gateway.exception.UnknownClientException;
import com.neoflex.gateway.exception.UnknownServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
                response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new UnknownServerException(
                    "проблемы на стороне стороннего сервера. Мы уже в курсе проблемы и скоро ее решим.");
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            switch (response.getStatusCode()) {
                case BAD_REQUEST ->
                        throw new BadRequestException("введенные данные невалидны. Проверьте их и повторите попытку.");
                case FORBIDDEN -> throw new InvalidSesCodeException("ses-код не прошел проверку.");
                case NOT_FOUND -> throw new ApplicationNotFoundException("заявка с нужным id не существует.");
                case CONFLICT -> throw new ScoringConveyorException("в выдаче кредита отказано.");
                default -> throw new UnknownClientException(
                        "проблемы на стороне клиента. Проверьте данные и повторите ваш запрос позже.");
            }
        }
    }
}
