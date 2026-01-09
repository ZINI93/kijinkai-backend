package com.kijinkai.domain.user.adapter.in.web.securiry;

import com.kijinkai.filter.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.UUID;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {


    private final AuthPrincipal principal;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;


    public UUID getUserUuid() {
        return principal.userUuid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return principal.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
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
