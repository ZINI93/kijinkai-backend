package com.kijinkai.domain.user.adapter.in.web.securiry;

import com.kijinkai.domain.user.adapter.in.web.securiry.dto.UserSecurityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserSecurityDto userSecurityDto;


    public Long getUserId() {
        return userSecurityDto.getUserId();
    }

    public UUID getUserUuid() {
        return userSecurityDto.getUserUuid();
    }

    public String getEmail() {
        return userSecurityDto.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userSecurityDto.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return userSecurityDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userSecurityDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
