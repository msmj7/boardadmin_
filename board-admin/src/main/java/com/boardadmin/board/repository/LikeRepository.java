package com.boardadmin.board.repository;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.model.Post;
import com.boardadmin.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
}
