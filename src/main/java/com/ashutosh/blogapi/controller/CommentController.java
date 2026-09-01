package com.ashutosh.blogapi.controller;

import com.ashutosh.blogapi.dto.CommentRequest;
import com.ashutosh.blogapi.dto.CommentResponse;
import com.ashutosh.blogapi.dto.NestedCommentRequest;
import com.ashutosh.blogapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/comments")
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(request));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createCommentForPost(
            @PathVariable Long postId,
            @Valid @RequestBody NestedCommentRequest request) {

        CommentRequest commentRequest = new CommentRequest(
                request.getAuthor(),
                request.getContent(),
                postId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(commentRequest));
    }

    @GetMapping("/api/comments/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPost(postId)
        );
    }

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPost(postId)
        );
    }

    @GetMapping("/api/comments/post/{postId}/approved")
    public ResponseEntity<List<CommentResponse>> getApprovedCommentsByPost(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getApprovedCommentsByPost(postId)
        );
    }

    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity.ok(
                commentService.updateComment(id, request)
        );
    }

    @PatchMapping("/api/comments/{id}/approve")
    public ResponseEntity<CommentResponse> approveComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.approveComment(id)
        );
    }

    @PatchMapping("/api/comments/{id}/reject")
    public ResponseEntity<CommentResponse> rejectComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.rejectComment(id)
        );
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}