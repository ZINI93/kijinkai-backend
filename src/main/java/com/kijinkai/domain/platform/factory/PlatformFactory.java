package com.kijinkai.domain.platform.factory;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class PlatformFactory {

    public Platform createPlatform(User user, PlatformRequestDto requestDto){

        return Platform.builder()
                .platformUuid(UUID.randomUUID())
                .user(user)
                .baseUrl(requestDto.getBaseUrl())
                .build();
    }
}
