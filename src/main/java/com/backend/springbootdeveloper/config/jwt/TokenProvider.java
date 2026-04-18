package com.backend.springbootdeveloper.config.jwt;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅을 위해 추가
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // 구체적인 SecretKey 타입으로 변경
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

@Slf4j // 로깅을 위해 추가
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final UserMapper userMapper;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    // Key -> SecretKey 로 타입 명확화
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // AccessToken 생성
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000L * 60 * 120); // 2시간

        return builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .claim("id", user.getUserId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                // [개선] 알고리즘을 명시하지 않아도 key 타입으로 자동 추론됩니다.
                .signWith(key)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 14); // 2주

        return builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .claim("id", user.getUserId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                // [개선] signWith 간소화
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validToken(String token) {
        try {
            if (token == null) return false;
            // [개선] 최신 파서 빌더 사용
            Jwts.parser()
                    .verifyWith(key) // 서명 키 검증
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // [개선] 예외 발생 시 로그를 남겨 디버깅을 용이하게 합니다.
            log.warn("유효하지 않은 JWT 토큰입니다. reason: {}", e.getMessage());
            return false;
        }
    }

    // JWT에서 Authentication 객체 추출 ( 여기서 CustomUserDetails 생성)
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String email = claims.getSubject();

        User user = userMapper.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + email));
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JWT에서 사용자 정보 꺼내기
    private Claims getClaims(String token) {
        // [개선] 최신 파서 빌더 사용
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}