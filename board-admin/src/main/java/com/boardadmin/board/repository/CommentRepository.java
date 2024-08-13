package com.boardadmin.board.repository;

import com.boardadmin.board.model.Comment;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostPostId(Long postId);
    List<Comment> findByPostPostIdOrderByCreatedAtDesc(Long postId);
    Page<Comment> findByContentContaining(String content, Pageable pageable);
    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    
    @Modifying
    @Transactional
    void deleteByPost_PostId(Long postId);
}
