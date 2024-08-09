package com.boardadmin.board.service;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.LikeRepository;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    public void likePost(Long postId, String userId) {
        User user = userService.getUserByUserId(userId);
        Post post = new Post();
        post.setPostId(postId);
        
        if (likeRepository.findByUserAndPost(user, post).isEmpty()) {
            Likes like = new Likes();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    public void unlikePost(Long postId, String userId) {
        User user = userService.getUserByUserId(userId);
        Post post = new Post();
        post.setPostId(postId);
        
        likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);
    }

    public boolean isPostLikedByUser(Long postId, String userId) {
        User user = userService.getUserByUserId(userId);
        Post post = new Post();
        post.setPostId(postId);
        
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }

    public int countLikes(Long postId) {
        Post post = new Post();
        post.setPostId(postId);
        post.setLikes(0);
        
        return likeRepository.countByPost(post);
    }
}
