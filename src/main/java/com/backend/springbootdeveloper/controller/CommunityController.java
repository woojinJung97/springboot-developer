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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 게시글 목록 조회 — 페이지네이션(page, pageSize)·키워드 검색 지원
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostsResponseDto>>> getPostsList(PostsRequestDto dto) {
        List<PostsResponseDto> result = communityService.getPostsList(dto);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", result));
    }

    // 게시글 작성 — 로그인 유저 ID를 자동으로 설정하여 저장
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPosts(@AuthenticationPrincipal CustomUserDetails user, @RequestBody PostsResponseDto dto) {
        communityService.createPosts(user, dto);
        return ResponseEntity.ok(ApiResponse.success("게시글 작성 완료", null));
    }

    // 게시글 상세 조회 — 호출 시 조회수 1 증가
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostsResponseDto>> getPostDetails(@PathVariable Long postId) {
        PostsResponseDto result = communityService.getPostsDetails(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 상세페이지 조회 성공", result));
    }

    // 댓글 작성 — 댓글 저장 후 해당 게시글의 댓글 수 1 증가 (트랜잭션)
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId, @RequestBody CommentRequestDto dto) {
        CommentResponseDto result = communityService.createComment(user, postId, dto);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", result));
    }

    // 댓글 목록 조회 — 특정 게시글의 모든 댓글 반환 (최신순)
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComment(@PathVariable Long postId) {
        List<CommentResponseDto> result = communityService.getComment(postId);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", result));
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<LikesResponseDto>> getLikes(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId) {
        LikesResponseDto result = communityService.getLikes(user, postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요 동작 성공", result));
    }

    /*
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> increaseLikes(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId) {
        List<CommentResponseDto> result = communityService.increaseLikes(user, postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요 동작 성공", result));
    }*/
}
