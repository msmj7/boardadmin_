package com.boardadmin.board.service;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.model.Post;
import com.boardadmin.user.model.User;
import com.boardadmin.board.repository.LikeRepository;
import com.boardadmin.board.repository.PostRepository;
import com.boardadmin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likesRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addLike(Long postId, Integer userIndex) {
        // 사용자를 userIndex로 검색
        User user = userRepository.findById(userIndex).orElseThrow(() -> new IllegalArgumentException("Invalid user index"));

        // 포스트를 postId로 검색
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 사용자가 이미 해당 포스트에 좋아요를 눌렀는지 확인
        if (!likesRepository.existsByUserAndPost(user, post)) {
            Likes like = new Likes();
            like.setUser(user);
            like.setPost(post);
            likesRepository.save(like);

            // 포스트의 좋아요 수 증가
            post.setLikes(post.getLikes() + 1);
            postRepository.save(post);
        }
    }

    @Transactional
    public void removeLike(Long postId, Integer userIndex) {
        // 사용자를 userIndex로 검색
        User user = userRepository.findById(userIndex).orElseThrow(() -> new IllegalArgumentException("Invalid user index"));

        // 포스트를 postId로 검색
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 사용자가 해당 포스트에 좋아요를 눌렀는지 확인
        Likes like = likesRepository.findByUserAndPost(user, post);
        if (like != null) {
            likesRepository.delete(like);

            // 포스트의 좋아요 수 감소
            post.setLikes(post.getLikes() - 1);
            postRepository.save(post);
        }
    }

    public boolean hasLiked(Long postId, Integer userIndex) {
        // 사용자를 userIndex로 검색
        User user = userRepository.findById(userIndex).orElseThrow(() -> new IllegalArgumentException("Invalid user index"));

        // 포스트를 postId로 검색
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 사용자가 해당 포스트에 좋아요를 눌렀는지 확인
        return likesRepository.existsByUserAndPost(user, post);
    }
}
