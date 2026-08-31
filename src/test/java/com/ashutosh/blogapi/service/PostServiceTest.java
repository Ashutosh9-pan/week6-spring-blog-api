package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.PostRequest;
import com.ashutosh.blogapi.dto.PostResponse;
import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CategoryRepository;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    private Category category;
    private Post post;
    private PostRequest request;

    @BeforeEach
    void setUp() {

        category = new Category(
                1L,
                "Technology",
                "Technology and programming articles"
        );

        request = new PostRequest(
                "Spring Boot REST API",
                "Building REST APIs using Spring Boot.",
                "Ashutosh",
                1L
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
    }

    @Test
    void shouldCreatePostSuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostResponse response = postService.createPost(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Spring Boot REST API", response.getTitle());
        assertEquals("Ashutosh", response.getAuthor());
        assertEquals(1L, response.getCategoryId());
        assertEquals("Technology", response.getCategoryName());

        verify(postRepository, times(1))
                .save(any(Post.class));
    }

    @Test
    void shouldGetPostByIdSuccessfully() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Spring Boot REST API", response.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {

        when(postRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getPostById(999L)
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundWhileCreatingPost() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        PostRequest invalidRequest = new PostRequest(
                "Test Post",
                "Test Content",
                "Ashutosh",
                999L
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPost(invalidRequest)
        );

        verify(postRepository, never())
                .save(any(Post.class));
    }

    @Test
    void shouldDeletePostSuccessfully() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(postRepository, times(1))
                .delete(post);
    }
}