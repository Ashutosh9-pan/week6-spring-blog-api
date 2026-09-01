package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.CommentRequest;
import com.ashutosh.blogapi.dto.CommentResponse;
import com.ashutosh.blogapi.entity.Comment;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CommentRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private static final Logger log =
            LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository) {

        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentResponse createComment(CommentRequest request) {

        log.info(
                "Creating comment for post id: {} by author: {}",
                request.getPostId(),
                request.getAuthor()
        );

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> {

                    log.warn(
                            "Comment creation failed. Post not found with id: {}",
                            request.getPostId()
                    );

                    return new ResourceNotFoundException(
                            "Post not found with id: " + request.getPostId()
                    );
                });

        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setApproved(false);

        Comment savedComment = commentRepository.save(comment);

        log.info(
                "Comment created successfully with id: {} for post id: {}",
                savedComment.getId(),
                post.getId()
        );

        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {

        log.debug("Fetching all comments for post id: {}", postId);

        if (!postRepository.existsById(postId)) {

            log.warn(
                    "Cannot fetch comments. Post not found with id: {}",
                    postId
            );

            throw new ResourceNotFoundException(
                    "Post not found with id: " + postId
            );
        }

        List<CommentResponse> comments =
                commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.debug(
                "Fetched {} comments for post id: {}",
                comments.size(),
                postId
        );

        return comments;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getApprovedCommentsByPost(Long postId) {

        log.debug(
                "Fetching approved comments for post id: {}",
                postId
        );

        if (!postRepository.existsById(postId)) {

            log.warn(
                    "Cannot fetch approved comments. Post not found with id: {}",
                    postId
            );

            throw new ResourceNotFoundException(
                    "Post not found with id: " + postId
            );
        }

        List<CommentResponse> comments =
                commentRepository
                        .findByPostIdAndApprovedTrueOrderByCreatedAtDesc(postId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.debug(
                "Fetched {} approved comments for post id: {}",
                comments.size(),
                postId
        );

        return comments;
    }

    public CommentResponse updateComment(
            Long id,
            CommentRequest request) {

        log.info("Updating comment with id: {}", id);

        Comment comment = getCommentById(id);

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> {

                    log.warn(
                            "Comment update failed. Post not found with id: {}",
                            request.getPostId()
                    );

                    return new ResourceNotFoundException(
                            "Post not found with id: " + request.getPostId()
                    );
                });

        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        comment.setPost(post);

        Comment updatedComment = commentRepository.save(comment);

        log.info(
                "Comment updated successfully with id: {}",
                id
        );

        return mapToResponse(updatedComment);
    }

    public CommentResponse approveComment(Long id) {

        log.info("Approving comment with id: {}", id);

        Comment comment = getCommentById(id);

        comment.setApproved(true);

        Comment updatedComment = commentRepository.save(comment);

        log.info(
                "Comment approved successfully with id: {}",
                id
        );

        return mapToResponse(updatedComment);
    }

    public CommentResponse rejectComment(Long id) {

        log.info("Rejecting comment with id: {}", id);

        Comment comment = getCommentById(id);

        comment.setApproved(false);

        Comment updatedComment = commentRepository.save(comment);

        log.info(
                "Comment rejected successfully with id: {}",
                id
        );

        return mapToResponse(updatedComment);
    }

    public void deleteComment(Long id) {

        log.info("Deleting comment with id: {}", id);

        Comment comment = getCommentById(id);

        commentRepository.delete(comment);

        log.info(
                "Comment deleted successfully with id: {}",
                id
        );
    }

    private Comment getCommentById(Long id) {

        return commentRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Comment not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Comment not found with id: " + id
                    );
                });
    }

    private CommentResponse mapToResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getAuthor(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.isApproved(),
                comment.getCreatedAt()
        );
    }
}