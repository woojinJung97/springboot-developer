package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.domain.RefreshToken;
import com.backend.springbootdeveloper.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenMapper refreshTokenMapper;

    // 토큰 문자열로 RefreshToken 엔티티 조회 — 없으면 예외
    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenMapper.findByRefreshToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    // 로그인 시 발급한 리프레시 토큰을 DB에 저장
    public void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();
        refreshTokenMapper.insertToken(refreshToken);
    }

    // 로그아웃 시 해당 유저의 리프레시 토큰 삭제
    public void deleteRefreshToken(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }
}
