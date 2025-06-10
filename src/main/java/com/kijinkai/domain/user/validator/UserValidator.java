package com.kijinkai.domain.user.validator;

import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.entity.UserRole;
import com.kijinkai.domain.user.exception.UserRoleValidateException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void requireAdminRole(User user){
        if (!user.getUserRole().equals(UserRole.ADMIN)){
            throw new UserRoleValidateException("Only Admin can access a platform");
        }
    }
}
