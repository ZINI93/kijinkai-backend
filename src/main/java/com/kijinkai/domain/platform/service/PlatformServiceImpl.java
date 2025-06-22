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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlatformServiceImpl implements PlatformService{

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;
    private final PlatformFactory factory;

    /**
     * 관리자의 Platform 생성 프로세스
     * @param userUuid
     * @param requestDto
     * @return 플렛폼 생성 응답 DTO
     */

    @Override @Transactional
    public PlatformResponseDto createPlatformWithValidate(String userUuid, PlatformRequestDto requestDto) {

        User user = findUserByUserUuidWithRequireAdminRole(userUuid);

        Platform platform = factory.createPlatform(user, requestDto);
        Platform savedPlatform = platformRepository.save(platform);

        return platformMapper.toResponse(savedPlatform);
    }

    /**
     * 관리자의 Platform 수정 프로세스
     * @param userUuid
     * @param platformUuid
     * @param updateDto
     * @return 플랫폼 수정 응답 DTO
     */
    @Override @Transactional
    public PlatformResponseDto updatePlatformWithValidate(String userUuid, String platformUuid, PlatformUpdateDto updateDto) {

        findUserByUserUuidWithRequireAdminRole(userUuid);

        Platform platform = findPlatformPlatformUuid(platformUuid);

        platform.updatePlatformBaseUrl(updateDto);
        return platformMapper.toResponse(platform);
    }

    /**
     * Platform 삭제
     * @param userUuid
     * @param platformUuid
     */

    @Override
    public void deletePlatform(String userUuid, String platformUuid) {
        findUserByUserUuidWithRequireAdminRole(userUuid);
        Platform platform = findPlatformPlatformUuid(platformUuid);

        platformRepository.delete(platform);
    }

    /**
     * Platform 전체 조회
     * @param platform
     * @param pageable
     * @return 전체조회 DTO
     */

    @Override
    public Page<PlatformResponseDto> getPlatforms(Pageable pageable) {
        Page<Platform> platforms = platformRepository.findAllByPlatform(pageable);

        return platforms.map(platformMapper::toResponse);
    }

    /**
     * Platform 정보 확인 프로세스
     * @param userUuid
     * @param platformUuid
     * @return Platform 정보 응답 DTO
     */

    @Override
    public PlatformResponseDto getPlatformInfo(String userUuid, String platformUuid) {
        findUserByUserUuidWithRequireAdminRole(userUuid);
        Platform platform = findPlatformPlatformUuid(platformUuid);

        return platformMapper.toResponse(platform);
    }


    private User findUserByUserUuidWithRequireAdminRole(String userUuid) {
        User user = userRepository.findByUserUuid(UUID.fromString(userUuid))
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid %s", userUuid)));
        userValidator.requireAdminRole(user);

        return user;
    }

    private Platform findPlatformPlatformUuid(String platformUuid) {
        return platformRepository.findByPlatformUuid(UUID.fromString(platformUuid))
                .orElseThrow(() -> new PlatformNotFoundException(String.format("Platform not found for platform uuid %s", platformUuid)));
    }

}
