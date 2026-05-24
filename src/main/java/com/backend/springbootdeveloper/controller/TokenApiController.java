package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.dto.CreateAccessTokenRequest;
import com.backend.springbootdeveloper.dto.CreateAccessTokenResponse;
import com.backend.springbootdeveloper.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;

    // 리프레시 토큰으로 새 액세스 토큰 재발급 — 토큰 만료 시 자동 갱신용
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) throws IllegalAccessException {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newAccessToken));
    }
}
