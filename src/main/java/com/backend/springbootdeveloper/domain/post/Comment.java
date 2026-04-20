package com.backend.springbootdeveloper.domain.post;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {

    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;
    private Date createdAt;

}
