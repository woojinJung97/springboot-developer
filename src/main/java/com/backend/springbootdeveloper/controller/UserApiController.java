package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.dto.AddUserRequest;
import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.dto.UserRequestDto;
import com.backend.springbootdeveloper.dto.UserResponseDto;
import com.backend.springbootdeveloper.mapper.UserMapper;
import com.backend.springbootdeveloper.service.RefreshTokenService;
import com.backend.springbootdeveloper.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Value("${app.secure-cookie:false}")
    private boolean secureCookie;

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

        boolean isProd = secureCookie;
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd)              // 개발(HTTP)이면 false, 프로덕션(HTTPS)이면 true
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite(isProd ? "None" : "Lax") // 로컬 테스트 시 Lax 권장
                .build();
        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login Success");
        response.put("user", user.getEmail());
        response.put("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        User user = null;

        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            user = userService.findByEmail(username);
        } else if (principal instanceof String) {
            String username = (String) principal;
            user = userService.findByEmail(username);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of(
                "user", Map.of(
                        "email", user.getEmail(),
                        "nickname", user.getNickname()
                )
        ));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // HttpOnly refresh cookie 삭제(만료)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // HTTPS 환경이면 true
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(200, "Logout success", null));
    }
    
    @PostMapping("/api/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody AddUserRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
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
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyHome(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        UserResponseDto result = userService.getMyHome(userId);

        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공", result));
    }

    // 회원정보 수정
    @PatchMapping("/api/users/myhome")
    public ResponseEntity<ApiResponse<UserResponseDto>> updatedUser(@AuthenticationPrincipal CustomUserDetails user,@RequestBody UserRequestDto dto) {
        UserResponseDto result = userService.updatedUser(user, dto);
        return ResponseEntity.ok(ApiResponse.success("회원정보 수정 완료", result));
    }

    // 회원 탈퇴
    @DeleteMapping("/api/users/myhome/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long userId) {
        userService.deleteUser(user, userId);

        return ResponseEntity.noContent().build();
    }

}
