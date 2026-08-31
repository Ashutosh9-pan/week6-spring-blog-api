package com.ashutosh.blogapi.controller;

import com.ashutosh.blogapi.dto.CommentRequest;
import com.ashutosh.blogapi.dto.CommentResponse;
import com.ashutosh.blogapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(request));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPost(postId)
        );
    }

    @GetMapping("/post/{postId}/approved")
    public ResponseEntity<List<CommentResponse>> getApprovedCommentsByPost(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getApprovedCommentsByPost(postId)
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<CommentResponse> approveComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.approveComment(id)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<CommentResponse> rejectComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.rejectComment(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return ResponseEntity.noContent().build();
    }
}