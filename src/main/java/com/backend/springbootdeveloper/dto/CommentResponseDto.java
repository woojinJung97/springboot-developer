package com.backend.springbootdeveloper.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Date createdAt;

}
