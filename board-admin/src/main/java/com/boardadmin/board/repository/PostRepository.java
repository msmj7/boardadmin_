package com.boardadmin.board.repository;

import com.boardadmin.board.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByBoard_BoardId(Long boardId, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
