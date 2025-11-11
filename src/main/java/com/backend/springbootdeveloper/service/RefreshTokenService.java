package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.domain.RefreshToken;
import com.backend.springbootdeveloper.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenMapper.findByRefreshToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();
        refreshTokenMapper.insertToken(refreshToken);
    }

    public void deleteRefreshToken(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

}
