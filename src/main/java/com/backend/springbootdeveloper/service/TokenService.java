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

    // 리프레시 토큰 유효성 검증 → DB에서 userId 조회 → 새 액세스 토큰 생성 후 반환
    public String createNewAccessToken(String refreshToken) throws IllegalAccessException {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalAccessException("유효하지 않은 토큰입니다.");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateAccessToken(user);
    }
}
