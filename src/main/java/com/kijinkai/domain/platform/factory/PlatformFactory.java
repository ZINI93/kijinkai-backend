package com.kijinkai.domain.platform.factory;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.user.entity.User;
import org.springframework.stereotype.Component;


@Component
public class PlatformFactory {

    public Platform createPlatform(User user, PlatformRequestDto requestDto){

        return Platform.builder()
                .user(user)
                .baseUrl(requestDto.getBaseUrl())
                .build();
    }
}
