package com.backend.springbootdeveloper.config;

import com.backend.springbootdeveloper.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userService;
    private final TokenProvider tokenProvider;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS를 맨 위에서 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // ✅ CSRF 비활성화 (SPA에서는 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)
                // ✅ 기본 폼 로그인 / 세션 로그인 비활성화 (Vue가 직접 API 호출할 거니까)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스
                        .requestMatchers("/css/**", "/js/**", "/static/**").permitAll()
                        // 인증 API (공개)
                        .requestMatchers(HttpMethod.POST, "/api/login", "/api/signup", "/api/token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/check-email", "/api/check-nickname").permitAll()
                        // 기차 조회 (읽기만 공개)
                        .requestMatchers(HttpMethod.GET, "/api/train/search", "/api/train/detail", "/api/train/train-station", "/api/train/seat-info").permitAll()
                        // 커뮤니티 읽기 (공개)
                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*", "/api/posts/*/comments").permitAll()
                        // 나머지 모두 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 지정
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));

        // 쿠키 세션 포함 요청 허용
        config.setAllowCredentials(true);

        // URL 패턴별 CORS 매핑하기
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // 비밀번호 암호화기 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager를 직접 Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}