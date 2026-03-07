package com.backend.springbootdeveloper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponseDto {

    private Long userId;
    @JsonProperty("menu_id")
    private int menuId;
    private Long postId;
    private String subcategory;
    private String title;
    private String content;
    private int commentCount;
    private int viewCount;
    private Date updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;

}
