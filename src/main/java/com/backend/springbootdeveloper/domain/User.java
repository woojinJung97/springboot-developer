package com.backend.springbootdeveloper.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long userId;        // DB: user_id
    private String email;       // DB: email
    private String nickname;    // DB: nickname
    private String password;    // DB: password
    private String rate;        // DB: rate (BRONZE/SILVER/GOLD)
    private String role;        // DB: role (USER/ADMIN)
    private LocalDateTime regDate;  // DB: reg_date

}
