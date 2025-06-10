package com.kijinkai.domain.user.service;


import com.kijinkai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("login access:{} ", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("Searching userEmail:{}",user.getEmail());
                    return new CustomUserDetails(user);
                })
                .orElseThrow(() -> {
                    log.error("user not found");
                    return new UsernameNotFoundException("User not found");
                });
    }
}
