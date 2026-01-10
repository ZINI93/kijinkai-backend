package com.kijinkai.domain.common;


import com.kijinkai.domain.common.service.DashBoardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/dashboard",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DashboardApiController  extends  BaseController{


    private final DashBoardService dashBoardService;

    @GetMapping("/count")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 count 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DashBoardCountResponseDto>> getDeliveriesCountByStatus(
            Authentication authentication
    ) {
        UUID userUuid = getUserUuid(authentication);
        DashBoardCountResponseDto response = dashBoardService.getDashboardCount(userUuid);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order payment information", response));
    }
}
