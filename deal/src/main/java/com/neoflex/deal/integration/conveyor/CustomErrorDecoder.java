package com.neoflex.deal.integration.conveyor;

import com.neoflex.deal.exception.BadRequestConveyorException;
import com.neoflex.deal.exception.ScoringConveyorException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new BadRequestConveyorException("Ошибка валидации введенных данных");
            case 409 -> new ScoringConveyorException("Отказ: ошибка скоринга");
            default -> new Exception("Непредвиденная ошибка сервиса Conveyor");
        };
    }
}
