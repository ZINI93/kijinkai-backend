package com.kijinkai.domain.common.api;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.common.dto.MyPageResponseDto;
import com.kijinkai.domain.common.service.MainPageService;
import com.kijinkai.domain.common.dto.MainPageResponseDto;
import com.kijinkai.domain.common.service.MyPageService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@RestController
public class CommonController {

    private final MainPageService mainPageService;
    private final MyPageService myPageService;

    @GetMapping("/main-page")
    public ResponseEntity<BasicResponseDto<MainPageResponseDto>> mainPage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MainPageResponseDto response = mainPageService.mainPage(customUserDetails.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful retrieved mainPage information", response));
    }


    @GetMapping("/my-page")
    public ResponseEntity<BasicResponseDto<MyPageResponseDto>> myPage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        MyPageResponseDto response = myPageService.myPage(customUserDetails.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful retrieved mainPage information", response));
    }
}
