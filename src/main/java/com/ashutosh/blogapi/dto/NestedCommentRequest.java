package com.ashutosh.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NestedCommentRequest {

    @NotBlank(message = "Author is required")
    @Size(min = 2, max = 100, message = "Author must be between 2 and 100 characters")
    private String author;

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String content;

    public NestedCommentRequest() {
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
}