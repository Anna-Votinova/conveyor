package com.neoflex.dossier.integration.deal;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return new Exception("Непредвиденная ошибка сервиса Deal");
    }
}
