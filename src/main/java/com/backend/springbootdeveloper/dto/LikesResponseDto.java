package com.backend.springbootdeveloper.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikesResponseDto {

    private String likeId;
    private String postId;
    private String userId;
    private String createAt;
    private int totalLikeCount;
}