package com.neoflex.deal.service.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SesCodeGeneratorUtil {

    private static final int STREAM_SIZE = 1;
    private static final int START_RANDOM_NUMBER = 1000;
    private static final int FINISH_RANDOM_NUMBER = 10000;

    public static Integer generateSesCode() {
        SecureRandom secureRandom = new SecureRandom();
        IntStream intStream = secureRandom.ints(STREAM_SIZE, START_RANDOM_NUMBER, FINISH_RANDOM_NUMBER);
        return intStream.findFirst().orElseThrow();
    }
}
