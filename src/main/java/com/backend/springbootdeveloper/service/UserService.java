package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.dto.AddUserRequest;
import com.backend.springbootdeveloper.dto.UserRequestDto;
import com.backend.springbootdeveloper.dto.UserResponseDto;
import com.backend.springbootdeveloper.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(AddUserRequest dto) {
        if (userMapper.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userMapper.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRate("BRONZE");
        user.setRole("USER");

        userMapper.insertUser(user);
    }

    // 로그인
    public String login(String email, String password) {
        User user = userMapper.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Access Token & Refresh Token 발급
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateAccessToken(user);

        // Refresh Token 저장
        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        return accessToken;
    }

    public User findById(Long userId) {
        return userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public UserResponseDto getMyHome(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updatedUser(CustomUserDetails userDetails, UserRequestDto dto) {
        userMapper.updatedUser(userDetails, dto);

        User updateUser = userMapper.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new UserResponseDto(updateUser);
    }

    public void deleteUser(CustomUserDetails user, Long userId) {
        userMapper.deleteUser(user, userId);
    }
}
