package com.kijinkai.domain.platform.service;


import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.platform.exception.PlatformNotFoundException;
import com.kijinkai.domain.platform.factory.PlatformFactory;
import com.kijinkai.domain.platform.mapper.PlatformMapper;
import com.kijinkai.domain.platform.repository.PlatformRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlatformServiceImpl implements PlatformService{

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PlatformRepository platformRepository;
    private final PlatformMapper mapper;
    private final PlatformFactory factory;

    @Override @Transactional
    public PlatformResponseDto createPlatformWithValidate(String userUuid, PlatformRequestDto requestDto) {

        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("UserUuid: User Not found"));

        validateUserRole(user);

        Platform platform = factory.createPlatform(user, requestDto);
        Platform savedPlatform = platformRepository.save(platform);

        return mapper.toResponse(savedPlatform);
    }

    private void updatePlatform(Platform platform, PlatformUpdateDto updateDto){
        platform.updatePlatform(updateDto.getBaseUrl());
    }

    @Override @Transactional
    public PlatformResponseDto updatePlatformWithValidate(String userUuid, String platformUuid, PlatformUpdateDto updateDto) {

        Platform platform = findPlatformByUserUuidAndPlatfomUuidByPlatform(userUuid, platformUuid);
        validateUserRole(platform.getUser());
        updatePlatform(platform,updateDto);
        return mapper.toResponse(platform);
    }

    @Override
    public void deletePlatform(String userUuid, String platformUuid) {
        Platform platform = findPlatformByUserUuidAndPlatfomUuidByPlatform(userUuid, platformUuid);
        validateUserRole(platform.getUser());
        platformRepository.delete(platform);
    }

    @Override
    public PlatformResponseDto getPlatformInfo(String userUuid, String platformUuid) {
        Platform platform = findPlatformByUserUuidAndPlatfomUuidByPlatform(userUuid, platformUuid);
        validateUserRole(platform.getUser());

        return mapper.toResponse(platform);
    }

    private Platform findPlatformByUserUuidAndPlatfomUuidByPlatform(String userUuid, String platformUuid) {
        return platformRepository.findByUserUserUuidAndPlatformUuid(userUuid, platformUuid)
                .orElseThrow(() -> new PlatformNotFoundException("UserUuidAndPlatformUUid: Platform not found"));
    }

    private void validateUserRole(User user) {
        userValidator.requireAdminRole(user);
    }

}
