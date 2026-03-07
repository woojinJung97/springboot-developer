package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.dto.PostsRequestDto;
import com.backend.springbootdeveloper.dto.PostsResponseDto;
import com.backend.springbootdeveloper.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 커뮤니티 목록 조회
    @GetMapping("/posts-list")
    public ResponseEntity<ApiResponse<List<PostsResponseDto>>> getPostsList(PostsRequestDto dto) {
        List<PostsResponseDto> result = communityService.getPostsList(dto);

        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", result));
    }

    // 커뮤니티 글 쓰기
    @PostMapping("/write")
    public void createPosts(@AuthenticationPrincipal CustomUserDetails user,@RequestBody PostsResponseDto dto) {
        communityService.createPosts(user, dto);
    }

    // 커뮤니티 상세
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostsResponseDto>> getPostDetails(@PathVariable Long postId) {
        PostsResponseDto result = communityService.getPostsDetails(postId);

        return ResponseEntity.ok(ApiResponse.success("게시글 상세페이지 조회 성공", result));
    }

}
