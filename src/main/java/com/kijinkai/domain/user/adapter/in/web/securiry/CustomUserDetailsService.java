package com.kijinkai.domain.user.adapter.in.web.securiry;


import com.kijinkai.domain.user.adapter.in.web.securiry.dto.UserSecurityDto;
import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
import com.kijinkai.domain.user.domain.exception.AlreadyVerifiedException;
import com.kijinkai.domain.user.domain.exception.EmailNotFoundException;
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
                .map(userJpaEntity -> {
                    log.info("Searching userEmail:{}",userJpaEntity.getEmail());

                    log.warn("Email not verified for user: {}", email);
                    if (!userJpaEntity.isEmailVerified()){
                        throw new EmailNotFoundException("이메일 인증이 필요합니다.");
                    }

                    UserSecurityDto userSecurityDto = UserSecurityDto.builder()
                            .userId(userJpaEntity.getUserId())
                            .userUuid(userJpaEntity.getUserUuid())
                            .email(userJpaEntity.getEmail())
                            .password(userJpaEntity.getPassword())
                            .userRole(userJpaEntity.getUserRole())
                            .build();

                    return new CustomUserDetails(userSecurityDto);
                })
                .orElseThrow(() -> {
                    log.error("user not found");
                    return new UsernameNotFoundException("User not found");
                });
    }
}
