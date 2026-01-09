package com.kijinkai.domain.user.application.service;

import com.kijinkai.domain.customer.application.port.in.CreateCustomerUseCase;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.dto.request.UserSignUpRequestDto;
import com.kijinkai.domain.user.application.dto.response.UserSignUpResponse;
import com.kijinkai.domain.user.application.mapper.UserMapper;
import com.kijinkai.domain.user.application.port.in.CreateUserUseCase;
import com.kijinkai.domain.user.application.port.in.SignUpUserUseCase;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.port.in.CreateWalletUseCase;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserSignUpService implements SignUpUserUseCase {

    private final CreateUserUseCase userUseCase;
    private final CreateCustomerUseCase customerUseCase;
    private final CreateWalletUseCase walletUseCase;


    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequestDto userSignUpRequestDto){

        User user = userUseCase.createUser(userSignUpRequestDto.getEmail(), userSignUpRequestDto.getPassword(), userSignUpRequestDto.getNickname());
        Customer customer = customerUseCase.createCustomer(user.getUserUuid(), userSignUpRequestDto.getFirstName(), userSignUpRequestDto.getLastName(), userSignUpRequestDto.getPhoneNumber());
        Wallet wallet = walletUseCase.createWallet(customer.getCustomerUuid());

        return userMapper.toSignUpResponse(user,customer, wallet);
    }
}
