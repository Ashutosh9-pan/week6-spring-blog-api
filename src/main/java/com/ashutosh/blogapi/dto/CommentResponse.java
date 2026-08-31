package com.ashutosh.blogapi.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private Long postId;
    private boolean approved;
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public CommentResponse(
            Long id,
            String author,
            String content,
            Long postId,
            boolean approved,
            LocalDateTime createdAt) {

        this.id = id;
        this.author = author;
        this.content = content;
        this.postId = postId;
        this.approved = approved;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}