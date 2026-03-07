package com.backend.springbootdeveloper.domain;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RefreshToken {
    private Long tokenId;
    private Long userId;
    private String token;
    private String createdAt;
}
