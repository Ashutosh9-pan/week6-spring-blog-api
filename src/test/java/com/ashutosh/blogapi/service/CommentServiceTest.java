package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.CommentRequest;
import com.ashutosh.blogapi.dto.CommentResponse;
import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.entity.Comment;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CommentRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment comment;
    private CommentRequest request;

    @BeforeEach
    void setUp() {

        Category category = new Category(
                1L,
                "Technology",
                "Technology and programming articles"
        );

        post = new Post(
                1L,
                "Spring Boot REST API",
                "Building REST APIs using Spring Boot.",
                "Ashutosh",
                category,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        request = new CommentRequest(
                "Rahul",
                "Great explanation!",
                1L
        );

        comment = new Comment(
                1L,
                "Rahul",
                "Great explanation!",
                post,
                false,
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateCommentSuccessfully() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentResponse response = commentService.createComment(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Rahul", response.getAuthor());
        assertEquals(1L, response.getPostId());
        assertFalse(response.isApproved());

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {

        when(postRepository.findById(999L))
                .thenReturn(Optional.empty());

        CommentRequest invalidRequest = new CommentRequest(
                "Rahul",
                "Test comment",
                999L
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(invalidRequest)
        );

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void shouldApproveCommentSuccessfully() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(commentRepository.save(comment))
                .thenReturn(comment);

        CommentResponse response = commentService.approveComment(1L);

        assertTrue(response.isApproved());

        verify(commentRepository, times(1))
                .save(comment);
    }

    @Test
    void shouldRejectCommentSuccessfully() {

        comment.setApproved(true);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(commentRepository.save(comment))
                .thenReturn(comment);

        CommentResponse response = commentService.rejectComment(1L);

        assertFalse(response.isApproved());
    }

    @Test
    void shouldDeleteCommentSuccessfully() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1))
                .delete(comment);
    }

    @Test
    void shouldThrowExceptionWhenCommentNotFound() {

        when(commentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.deleteComment(999L)
        );
    }
}