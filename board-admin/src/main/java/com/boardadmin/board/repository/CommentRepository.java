package com.boardadmin.board.repository;

import com.boardadmin.board.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostPostId(Long postId);
    Page<Comment> findByContentContaining(String content, Pageable pageable);
}
