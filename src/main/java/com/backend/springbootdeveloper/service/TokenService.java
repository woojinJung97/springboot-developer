package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import com.backend.springbootdeveloper.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // Refresh Token을 받아 Access Token 재발급
    public String createNewAccessToken(String refreshToken) throws IllegalAccessException {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalAccessException("유효하지 않은 토큰입니다.");
        }

        // refresh_token 테이블에서 userId 조회
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateAccessToken(user);
    }
}
