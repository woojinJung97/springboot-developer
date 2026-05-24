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

    public int getOffset() {
        int p = page < 1 ? 1 : page;
        int size = pageSize < 1 ? 10 : pageSize;
        return (p - 1) * size;
    }
}
