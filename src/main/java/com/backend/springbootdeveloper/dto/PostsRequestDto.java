package com.backend.springbootdeveloper.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostsRequestDto {

    private int page;
    private int pageSize;
    private String keyword;

}
