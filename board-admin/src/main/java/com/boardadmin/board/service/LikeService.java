package com.boardadmin.board.service;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.repository.LikeRepository;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.UserRepository;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public void likePost(Integer userIndex, Long postId) {
        User user = userRepository.findById(userIndex).orElseThrow(() -> new IllegalArgumentException("Invalid user index"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post id"));

        Likes like = likeRepository.findByUser_UserIndexAndPost_PostId(userIndex, postId)
                .orElse(new Likes());
        like.setUser(user);
        like.setPost(post);

        if (like.isLiked()) {
            like.setLiked(false); // 좋아요를 이미 누른 경우, 좋아요 취소
        } else {
            like.setLiked(true); // 좋아요 설정
            like.setDisliked(false); // 싫어요 해제
        }
        likeRepository.save(like);
    }

    public void dislikePost(Integer userIndex, Long postId) {
        User user = userRepository.findById(userIndex).orElseThrow(() -> new IllegalArgumentException("Invalid user index"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post id"));

        Likes like = likeRepository.findByUser_UserIndexAndPost_PostId(userIndex, postId)
                .orElse(new Likes());
        like.setUser(user);
        like.setPost(post);

        if (like.isDisliked()) {
            like.setDisliked(false); // 싫어요를 이미 누른 경우, 싫어요 취소
        } else {
            like.setDisliked(true); // 싫어요 설정
            like.setLiked(false); // 좋아요 해제
        }
        likeRepository.save(like);
    }

    public Optional<Likes> getUserLikeForPost(Integer userIndex, Long postId) {
        return likeRepository.findByUser_UserIndexAndPost_PostId(userIndex, postId);
    }
}
