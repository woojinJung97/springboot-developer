package com.backend.springbootdeveloper.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;

}
