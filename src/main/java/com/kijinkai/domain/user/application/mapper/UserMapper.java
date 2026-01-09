package com.kijinkai.domain.user.application.mapper;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.dto.response.UserSignUpResponse;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSignUpResponse toSignUpResponse(User user, Customer customer, Wallet wallet){
        return UserSignUpResponse.builder()
                .UserUuid(user.getUserUuid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .walletUuid(wallet.getWalletUuid())
                .createdAt(user.getCreatedAt())

                .build();
    }

    //동일 함으로 아래 response 수정필요

    public UserResponseDto toResponse(User user){

        return UserResponseDto.builder()
                .userUuid(user.getUserUuid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

    }
}
