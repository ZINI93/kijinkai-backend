package com.kijinkai.domain.common;

import com.kijinkai.domain.user.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public abstract class BaseController {

    protected UUID getUserUuid(Authentication authentication){
        if (authentication == null && authentication.getPrincipal() == null){
            throw new IllegalArgumentException("Authentication required");
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getUserUuid();
    }
}
