package com.neoflex.conveyor.service.utils;

import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;

@Service
public class TimeUtil {

    public GregorianCalendar getCurrentDate() {
        return new GregorianCalendar();
    }
}
