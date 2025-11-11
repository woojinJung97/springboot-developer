package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.dto.AddUserRequest;
import com.backend.springbootdeveloper.dto.AddUserResponse;
import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.mapper.UserMapper;
import com.backend.springbootdeveloper.service.RefreshTokenService;
import com.backend.springbootdeveloper.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) throws IllegalAccessException {
        String email = request.get("email").toString();
        String password = request.get("password").toString();

        User user = userService.findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalAccessException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateAccessToken(user);

        // DB에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login Success");
        response.put("user", user.getEmail());
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }
    
    @PostMapping("/api/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody AddUserRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", request));
    }

    // 이메일 중복 확인
    @GetMapping("/api/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userMapper.existsByEmail(email);
        return ResponseEntity.ok(Map.of("available", !exists,
                "message", exists ? "이미 사용중인 이메일입니다." : "사용 가능한 이메일입니다."));
    }

    // 닉네임 중복 확인
    @GetMapping("/api/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = userMapper.existsByNickname(nickname);
        return ResponseEntity.ok(Map.of("available", !exists,
                "message", exists ? "이미 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다."));
    }

    // 내 정보 조회
    @GetMapping("/api/users/myhome")
    public ResponseEntity<ApiResponse<?>> getMyHome(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        AddUserResponse userInfo = userService.getMyHome(userId);

        return ResponseEntity.ok(ApiResponse.success("내 정보 조회", userInfo));
    }

    // 내 정보 수정
    @PatchMapping("/api/users/myhome")
    public ResponseEntity<ApiResponse<AddUserResponse>> updateMyHome(@AuthenticationPrincipal CustomUserDetails user, @RequestBody AddUserRequest request) {
        AddUserResponse response = userService.updateMyHome(user,request);

        return ResponseEntity.ok(ApiResponse.success("내 정보 변경 완료", response));
    }
}
