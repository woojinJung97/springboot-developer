package com.backend.springbootdeveloper.dto;

import com.backend.springbootdeveloper.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private String rate;
    private LocalDateTime regDate;

    public AddUserResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.rate = user.getRate();
        this.regDate = user.getRegDate();
    }

    public AddUserResponse(String email, String nickname, String rate, String role) {
    }
}
