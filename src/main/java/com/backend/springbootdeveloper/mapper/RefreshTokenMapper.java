package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {
    void insertToken(RefreshToken refreshToken);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);
    void updateToken(RefreshToken refreshToken);
}
