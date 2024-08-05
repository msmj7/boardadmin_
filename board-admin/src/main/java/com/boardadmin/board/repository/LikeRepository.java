package com.boardadmin.board.repository;

import com.boardadmin.board.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUser_UserIndexAndPost_PostId(Integer userIndex, Long postId);
}
