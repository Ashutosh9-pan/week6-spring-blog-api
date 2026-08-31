package com.ashutosh.blogapi.repository;

import com.ashutosh.blogapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Post> findByAuthorIgnoreCase(String author, Pageable pageable);
}