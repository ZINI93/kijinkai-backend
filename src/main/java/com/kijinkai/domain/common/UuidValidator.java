package com.kijinkai.domain.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class UuidValidator {

    public UUID parseUuid(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "유효하지 않는 UUID 형식입니다: " + uuidString
            );
        }
    }
}
