package com.kijinkai.domain.user.application.validator;

import com.kijinkai.domain.user.application.dto.request.UserUpdateDto;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.InvalidUserDataException;
import com.kijinkai.domain.user.domain.exception.UserUpdateException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserPersistencePort userPersistencePort;

    public void validateCreateUserRequest(String email, String password, String nickname){
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    public void validateUpdateUserRequest(UserUpdateDto updateDto){
        validatePassword(updateDto.getCurrentPassword());
        validateNickname(updateDto.getNickname());
    }


    public void validateUserPassword(PasswordEncoder passwordEncoder, UserUpdateDto updateDto, User user){
        if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
            throw new UserUpdateException("Not match password");
        }
    }



    private void validateEmail(String email){
        if (email == null || email.trim().isEmpty()){
            throw new InvalidUserDataException("Email is required");
        }

    }


    // 비밀번호에 대한 검증
    private void validatePassword(String password){
        if (password == null || password.trim().isEmpty() || password.length() < 8){
            throw new InvalidUserDataException("Password is required");
        }

    }

    // 닉네임에 대한 검증
    private void validateNickname(String nickName){
        if (nickName == null || nickName.trim().isEmpty()){
            throw new InvalidUserDataException("Nickname is required");
        }
    }


}
