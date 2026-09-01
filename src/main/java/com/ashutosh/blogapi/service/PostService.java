package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.PostRequest;
import com.ashutosh.blogapi.dto.PostResponse;
import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CategoryRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private static final Logger log =
            LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostService(
            PostRepository postRepository,
            CategoryRepository categoryRepository) {

        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public PostResponse createPost(PostRequest request) {

        log.info(
                "Creating post with title: {} for category id: {}",
                request.getTitle(),
                request.getCategoryId()
        );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {

                    log.warn(
                            "Post creation failed. Category not found with id: {}",
                            request.getCategoryId()
                    );

                    return new ResourceNotFoundException(
                            "Category not found with id: "
                                    + request.getCategoryId()
                    );
                });

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(request.getAuthor());
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        log.info(
                "Post created successfully with id: {} and title: {}",
                savedPost.getId(),
                savedPost.getTitle()
        );

        return mapToResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {

        log.debug(
                "Fetching posts. Page: {}, Size: {}, Sort: {}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );

        Page<PostResponse> posts = postRepository.findAll(pageable)
                .map(this::mapToResponse);

        log.debug(
                "Fetched {} posts from page {}",
                posts.getNumberOfElements(),
                pageable.getPageNumber()
        );

        return posts;
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {

        log.debug("Fetching post with id: {}", id);

        Post post = getPostEntityById(id);

        return mapToResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByCategory(
            Long categoryId,
            Pageable pageable) {

        log.debug(
                "Fetching posts for category id: {}. Page: {}, Size: {}",
                categoryId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (!categoryRepository.existsById(categoryId)) {

            log.warn(
                    "Cannot fetch posts. Category not found with id: {}",
                    categoryId
            );

            throw new ResourceNotFoundException(
                    "Category not found with id: " + categoryId
            );
        }

        Page<PostResponse> posts =
                postRepository.findByCategoryId(categoryId, pageable)
                        .map(this::mapToResponse);

        log.debug(
                "Fetched {} posts for category id: {}",
                posts.getNumberOfElements(),
                categoryId
        );

        return posts;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByAuthor(
            String author,
            Pageable pageable) {

        log.debug(
                "Fetching posts by author: {}. Page: {}, Size: {}",
                author,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<PostResponse> posts =
                postRepository.findByAuthorIgnoreCase(author, pageable)
                        .map(this::mapToResponse);

        log.debug(
                "Fetched {} posts for author: {}",
                posts.getNumberOfElements(),
                author
        );

        return posts;
    }

    public PostResponse updatePost(Long id, PostRequest request) {

        log.info("Updating post with id: {}", id);

        Post post = getPostEntityById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {

                    log.warn(
                            "Post update failed. Category not found with id: {}",
                            request.getCategoryId()
                    );

                    return new ResourceNotFoundException(
                            "Category not found with id: "
                                    + request.getCategoryId()
                    );
                });

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(request.getAuthor());
        post.setCategory(category);

        Post updatedPost = postRepository.saveAndFlush(post);

        log.info(
                "Post updated successfully with id: {}",
                updatedPost.getId()
        );

        return mapToResponse(updatedPost);
    }

    public void deletePost(Long id) {

        log.info("Deleting post with id: {}", id);

        Post post = getPostEntityById(id);

        postRepository.delete(post);

        log.info("Post deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public Post getPostEntityById(Long id) {

        return postRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Post not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Post not found with id: " + id
                    );
                });
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