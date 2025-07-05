package com.kijinkai.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EmailRandomCode {

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
