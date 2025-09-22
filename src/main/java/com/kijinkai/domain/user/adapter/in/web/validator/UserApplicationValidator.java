package com.kijinkai.domain.user.adapter.in.web.validator;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.user.domain.exception.UserRoleValidateException;
import org.springframework.stereotype.Component;

@Component
public class UserApplicationValidator {

    public void requireAdminRole(User user){

        if (!user.getUserRole().equals(UserRole.ADMIN)){
            throw new UserRoleValidateException("Only Admin can access a platform");
        }
    }


    public void requireJpaAdminRole(UserJpaEntity user){

        if (!user.getUserRole().equals(UserRole.ADMIN)){
            throw new UserRoleValidateException("Only Admin can access a platform");
        }
    }
}
