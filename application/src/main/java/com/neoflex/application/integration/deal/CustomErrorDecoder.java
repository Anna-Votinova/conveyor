package com.neoflex.application.integration.deal;

import com.neoflex.application.exception.BadRequestException;
import com.neoflex.application.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new BadRequestException("Ошибка валидации в сервисе Deal");
            case 404 -> new NotFoundException("Ошибка в сервисе Deal - заявка не найдена");
            default -> new Exception("Непредвиденная ошибка сервиса Deal");
        };
    }
}
