package com.kijinkai.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Convert
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final String secretKey;

    public AttributeEncryptor(@Value("${encryption.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return AesUtils.encrypt(attribute, secretKey);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return AesUtils.decrypt(dbData, secretKey);
    }
}
