package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.dto.AddUserRequest;
import com.backend.springbootdeveloper.dto.AddUserResponse;
import com.backend.springbootdeveloper.dto.TrainReservDto;
import com.backend.springbootdeveloper.dto.TrainReservResponseDto;
import com.backend.springbootdeveloper.mapper.TrainReservMapper;
import com.backend.springbootdeveloper.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TrainReservMapper trainReservMapper;

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
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AddUserResponse getMyHome(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new AddUserResponse(user);
    }

    @Transactional
    public AddUserResponse updateMyHome(CustomUserDetails userDetails, AddUserRequest request) {
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(encodedPassword);
        }

        userMapper.updateMyHome(userDetails, request);

        User updatedUser = userMapper.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new AddUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(CustomUserDetails user, Long userId) {
        userMapper.deleteUser(user, userId);
    }

    public List<TrainReservResponseDto> getMyReserv(CustomUserDetails user) {
        Long userId = user.getUserId();

        return userMapper.getMyReserv(userId);
    }
}
