package com.kijinkai.domain.platform.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/platforms",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PlatformApiController {

    private final PlatformService platformService;


    /**
     * 플렛폼 리스트 조회
     * @param pageable
     * @return
     */
    @GetMapping()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "플렛폼 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플렛폼을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<PlatformResponseDto>>> getPlatforms(
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<PlatformResponseDto> platforms = platformService.getPlatforms(pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platforms", platforms));
    }

    /**
     * 플렛폼 정보 조회
     * @param platformUuid
     * @return
     */
    @GetMapping("/{platformUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "플렛폼 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플렛폼을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<PlatformResponseDto>> getPlatformInfo(
            @PathVariable UUID platformUuid
    ){
        PlatformResponseDto platform = platformService.getPlatformInfo(platformUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", platform));
    }


}
