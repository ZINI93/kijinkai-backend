package com.kijinkai.domain.customer.application.validator;

import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.user.domain.exception.InvalidUserDataException;
import org.springframework.stereotype.Component;

@Component
public class CustomerApplicationValidator {

    public void validateCreateCustomerRequest(CustomerRequestDto customerRequestDto) {
        validateFirstName(customerRequestDto.getFirstName());
        validateLastName(customerRequestDto.getLastName());
        validatePhoneNumber(customerRequestDto.getPhoneNumber());
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidUserDataException("First name is required");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidUserDataException("last name is required");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {

        // 1. null check
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new InvalidUserDataException("phoneNumber is required");
        }
        // 2. 숫자 및 하이픈(-) 외 다른 문자 제거
        String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9-]", "");


        // 3. 정규 표현식을 사용한 유효성 검사
        // 예: 080-1234-5678
        // ^0(1[016789])\d{3,4}\d{4}$|^02\d{7,8}$|^0(3[1-3]|4[1-4]|5[1-5]|6[1-4])\d{7,8}$
        String regex = "^(0[789]0-\\d{4}-\\d{4}|0[1-9][0-9]?-\\d{1,4}-\\d{4}|0120-\\d{3}-\\d{3}|0800-\\d{3}-\\d{3})$";
        if (!phoneNumber.matches(regex)) {
            throw new InvalidUserDataException("유효하지 않은 일본 전화번호 형식입니다.");
        }
    }


}
