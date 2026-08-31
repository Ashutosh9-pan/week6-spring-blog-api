package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.PostRequest;
import com.ashutosh.blogapi.dto.PostResponse;
import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CategoryRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostService(
            PostRepository postRepository,
            CategoryRepository categoryRepository) {

        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public PostResponse createPost(PostRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(request.getAuthor());
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        return mapToResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {

        return postRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {

        Post post = getPostEntityById(id);

        return mapToResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByCategory(
            Long categoryId,
            Pageable pageable) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    "Category not found with id: " + categoryId
            );
        }

        return postRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByAuthor(
            String author,
            Pageable pageable) {

        return postRepository.findByAuthorIgnoreCase(author, pageable)
                .map(this::mapToResponse);
    }

    public PostResponse updatePost(Long id, PostRequest request) {

        Post post = getPostEntityById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(request.getAuthor());
        post.setCategory(category);

        Post updatedPost = postRepository.saveAndFlush(post);

        return mapToResponse(updatedPost);
    }

    public void deletePost(Long id) {

        Post post = getPostEntityById(id);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Post getPostEntityById(Long id) {

        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id
                ));
    }

    private PostResponse mapToResponse(Post post) {

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCategory().getId(),
                post.getCategory().getName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}