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

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 이메일·닉네임 중복 확인 후 비밀번호 BCrypt 암호화하여 BRONZE 등급으로 신규 유저 저장
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

    // 이메일·비밀번호 검증 후 액세스/리프레시 토큰 발급 (TokenProvider 사용)
    public String login(String email, String password) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateAccessToken(user);

        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        return accessToken;
    }

    // userId로 유저 조회 — 없으면 예외
    public User findById(Long userId) {
        return userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    // 이메일로 유저 조회 — 없으면 예외
    public User findByEmail(String email) {
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    // 마이페이지용 유저 정보 조회 — User 엔티티를 UserResponseDto로 변환하여 반환
    public UserResponseDto getMyHome(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new UserResponseDto(user);
    }

    // 프로필 수정 — 비밀번호 입력 시 재암호화 처리 후 저장하고 갱신된 정보 반환
    @Transactional
    public UserResponseDto updatedUser(CustomUserDetails userDetails, UserRequestDto dto) {
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userMapper.updatedUser(userDetails, dto);

        User updateUser = userMapper.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new UserResponseDto(updateUser);
    }

    // 회원 탈퇴 — 본인 userId 일치 확인 후 계정 삭제
    public void deleteUser(CustomUserDetails user, Long userId) {
        userMapper.deleteUser(user, userId);
    }
}
