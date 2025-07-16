package com.kijinkai.domain.platform.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.platform.service.PlatformService;
import com.kijinkai.domain.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@Slf4j
@PreAuthorize("hasRole('ADMIN') and @securityService.canAccessPlatform(#platformUuid)")
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/platforms",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PlatformAdminApiController {

    private final PlatformService platformService;


    /**
     * 관리자가 플렛폼 등록
     * @param authentication
     * @param requestDto
     * @return
     */
    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자가 플렛폼 등록 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플렛폼을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<PlatformResponseDto>> createPlatform(
            Authentication authentication,
            @Valid @RequestBody PlatformRequestDto requestDto){

        UUID adminUuid = getUserUuid(authentication);
        log.info("Admin: {} requests platform creation", adminUuid);

        PlatformResponseDto platform = platformService.createPlatformWithValidate(adminUuid, requestDto);
        log.info("Platform creation completed: {}", platform.getPlatformUuid());


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/platforms/{platformUuid}")
                .buildAndExpand(platform.getPlatformUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created platform", platform));
    }

    /**
     * 플렛폼 수정
     * @param authentication
     * @param platformUuid
     * @param updateDto
     * @return
     */
    @PutMapping("/{platformUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자가 플렛폼 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플렛폼을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<PlatformResponseDto>> updatePlatform(
            Authentication authentication,
            @PathVariable UUID platformUuid,
            @Valid @RequestBody PlatformUpdateDto updateDto
            ){

        UUID adminUuid = getUserUuid(authentication);
        log.info("Admin: {} requests platform update", adminUuid);

        PlatformResponseDto platform = platformService.updatePlatformWithValidate(adminUuid, platformUuid, updateDto);
        log.info("Platform: {} update completed", platform.getPlatformUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful updated platform", platform));
    }


    @DeleteMapping("/{platformUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자가 플렛폼 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플렛폼을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<Void> deletePlatform(
            @PathVariable UUID platformUuid,
            Authentication authentication
    ){
        UUID adminUuid = getUserUuid(authentication);
        log.info("Admin: {} requests platform deletion", adminUuid);


        platformService.deletePlatform(adminUuid,platformUuid);
        log.info("Platform: {} deletion completed", platformUuid);


        return ResponseEntity.noContent().build();
    }


    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
