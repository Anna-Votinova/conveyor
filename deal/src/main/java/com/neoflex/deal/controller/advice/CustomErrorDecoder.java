package com.neoflex.deal.controller.advice;

import com.neoflex.deal.exception.BadRequestException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 400) {
            return new BadRequestException("Ошибка валидации в сервисе Conveyor");
        }
        return new Exception("Непредвиденная ошибка сервиса Conveyor");
    }
}
