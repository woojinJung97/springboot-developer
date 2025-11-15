package com.backend.springbootdeveloper.dto;

import com.backend.springbootdeveloper.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private String rate;
    private LocalDateTime regDate;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.rate = user.getRate();
        this.regDate = user.getRegDate();
    }

    public UserResponseDto(String email, String nickname, String role, String rate, LocalDateTime regDate) {
    }
}
