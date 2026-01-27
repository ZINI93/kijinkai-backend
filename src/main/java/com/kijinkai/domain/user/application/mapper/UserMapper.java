package com.kijinkai.domain.user.application.mapper;

import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.dto.response.UserEditInfoResponse;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.dto.response.UserSignUpResponse;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

    public UserResponseDto updatedResponse(UUID userUuid, UUID customerUuid){
        return UserResponseDto.builder()
                .userUuid(userUuid)
                .customerUuid(customerUuid)
                .build();
    }

    public UserEditInfoResponse toEditResponse(User user, Customer customer){

        return UserEditInfoResponse
                .builder()
                .email(user.getEmail())
                .nickName(user.getNickname())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .pcc(customer.getPcc())
                .phoneNumber(customer.getPhoneNumber())
                .bankType(customer.getBankType())
                .accountHolder(customer.getAccountHolder())
                .accountNumber(customer.getAccountNumber())
                .build();
    }
}
