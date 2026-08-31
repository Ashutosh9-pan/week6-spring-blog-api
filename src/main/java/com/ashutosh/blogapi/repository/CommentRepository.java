package com.ashutosh.blogapi.repository;

import com.ashutosh.blogapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findByPostIdAndApprovedTrueOrderByCreatedAtDesc(Long postId);
}