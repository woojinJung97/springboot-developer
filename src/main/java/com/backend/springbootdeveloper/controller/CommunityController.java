package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.*;
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

    // 댓글 쓰기
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(@AuthenticationPrincipal CustomUserDetails user,@PathVariable Long postId ,@RequestBody CommentRequestDto dto) {
        CommentResponseDto result = communityService.createComment(user, postId, dto);

        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", result));
    }

    // 댓글 조회
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComment(@PathVariable Long postId) {
        List<CommentResponseDto> result = communityService.getComment(postId);

        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", result));
    }
}
