package com.kijinkai.domain.platform.service;


import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.platform.exception.PlatformCreationException;
import com.kijinkai.domain.platform.exception.PlatformDeletionException;
import com.kijinkai.domain.platform.exception.PlatformNotFoundException;
import com.kijinkai.domain.platform.exception.PlatformUpdateException;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * @param adminUuid
     * @param requestDto
     * @return 플렛폼 생성 응답 DTO
     */

    @Override @Transactional
    public PlatformResponseDto createPlatformWithValidate(UUID adminUuid, PlatformRequestDto requestDto) {
        log.info("Admin: {} requests platform creation", adminUuid);

        User user = findUserByUserUuidWithRequireAdminRole(adminUuid);

        try {
            Platform platform = factory.createPlatform(user, requestDto);
            Platform savedPlatform = platformRepository.save(platform);

            log.info("Platform creation completed: {}", savedPlatform.getPlatformUuid());
            return platformMapper.toResponse(savedPlatform);
        }catch (Exception e){
            log.info("Error creating platform");
            throw new PlatformCreationException("Error creating platform");
        }
    }

    /**
     * 관리자의 Platform 수정 프로세스
     * @param adminUuid
     * @param platformUuid
     * @param updateDto
     * @return 플랫폼 수정 응답 DTO
     */
    @Override @Transactional
    public PlatformResponseDto updatePlatformWithValidate(UUID adminUuid, UUID platformUuid, PlatformUpdateDto updateDto) {

        log.info("Admin: {} requests platform update", adminUuid);

        findUserByUserUuidWithRequireAdminRole(adminUuid);
        Platform platform = findPlatformByPlatformUuid(platformUuid);

        try{
            platform.updatePlatformBaseUrl(updateDto);
            log.info("Platform: {} update completed", platform.getPlatformUuid());

            return platformMapper.toResponse(platform);
        }catch (Exception e){
            throw new PlatformUpdateException("Error updating platform");
        }

    }

    /**
     * Platform 삭제
     * @param adminUuid
     * @param platformUuid
     */

    @Override @Transactional
    public void deletePlatform(UUID adminUuid, UUID platformUuid) {

        log.info("Admin: {} requests platform deletion", adminUuid);

        findUserByUserUuidWithRequireAdminRole(adminUuid);

        try {
            Platform platform = findPlatformByPlatformUuid(platformUuid);
            log.info("Platform: {} deletion completed", platform.getPlatformUuid());
            platformRepository.delete(platform);
        }catch (Exception e){
            throw new PlatformDeletionException("Error deleting platform");
        }
    }

    /**
     * platform 전체 조회
     * @return
     */

    @Override
    public Page<PlatformResponseDto> getPlatforms(Pageable pageable) {
        log.debug("Request full platform inquiry");

        Page<Platform> platforms = platformRepository.findAll(pageable);
        log.info("Platforms search platform");

        return platforms.map(platformMapper::toResponse);
    }

    /**
     * Platform 정보 확인 프로세스
     * @param platformUuid
     * @return Platform 정보 응답 DTO
     */

    @Override
    public PlatformResponseDto getPlatformInfo(UUID platformUuid) {
        log.info("Platform {} info inquiry request", platformUuid);

        Platform platform = findPlatformByPlatformUuid(platformUuid);
        log.info("Platform info search platform");
        return platformMapper.toResponse(platform);
    }


    private User findUserByUserUuidWithRequireAdminRole(UUID userUuid) {
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid %s", userUuid)));
        userValidator.requireAdminRole(user);

        return user;
    }

    private Platform findPlatformByPlatformUuid(UUID platformUuid) {
        return platformRepository.findByPlatformUuid(platformUuid)
                .orElseThrow(() -> new PlatformNotFoundException(String.format("Platform not found for platform uuid %s", platformUuid)));
    }

}
