package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.dto.CommentRequest;
import com.ashutosh.blogapi.dto.CommentResponse;
import com.ashutosh.blogapi.entity.Comment;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CommentRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository) {

        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentResponse createComment(CommentRequest request) {

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + request.getPostId()
                ));

        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setApproved(false);

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException(
                    "Post not found with id: " + postId
            );
        }

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getApprovedCommentsByPost(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException(
                    "Post not found with id: " + postId
            );
        }

        return commentRepository
                .findByPostIdAndApprovedTrueOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CommentResponse approveComment(Long id) {

        Comment comment = getCommentById(id);

        comment.setApproved(true);

        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    public CommentResponse rejectComment(Long id) {

        Comment comment = getCommentById(id);

        comment.setApproved(false);

        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    public void deleteComment(Long id) {

        Comment comment = getCommentById(id);

        commentRepository.delete(comment);
    }

    private Comment getCommentById(Long id) {

        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + id
                ));
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