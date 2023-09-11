package com.neoflex.deal.service.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SesCodeGeneratorUtil {

    private static final int START_RANDOM_NUMBER = 1000;
    private static final int FINISH_RANDOM_NUMBER = 10000;
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    public static Integer generateSesCode() {
        return RANDOM_GENERATOR.nextInt(START_RANDOM_NUMBER, FINISH_RANDOM_NUMBER);
    }
}
