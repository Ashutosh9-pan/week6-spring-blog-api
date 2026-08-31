package com.ashutosh.blogapi.controller;

import com.ashutosh.blogapi.dto.PostRequest;
import com.ashutosh.blogapi.dto.PostResponse;
import com.ashutosh.blogapi.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(request));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {

        return ResponseEntity.ok(
                postService.getAllPosts(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                postService.getPostById(id)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostResponse>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {

        return ResponseEntity.ok(
                postService.getPostsByCategory(categoryId, pageable)
        );
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<Page<PostResponse>> getPostsByAuthor(
            @PathVariable String author,
            Pageable pageable) {

        return ResponseEntity.ok(
                postService.getPostsByAuthor(author, pageable)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {

        return ResponseEntity.ok(
                postService.updatePost(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id) {

        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }
}