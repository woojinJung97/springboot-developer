package com.backend.springbootdeveloper.config.jwt;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final UserMapper userMapper;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    private Key key;

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
                .signWith(SignatureAlgorithm.HS256, key)
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
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validToken(String token) {
        try {
            if (token == null) return false;
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
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
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}