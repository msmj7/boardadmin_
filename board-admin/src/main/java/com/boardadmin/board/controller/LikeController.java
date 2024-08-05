package com.boardadmin.board.controller;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.service.LikeService;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/like/{postId}")
    public void toggleLike(@PathVariable Long postId, Principal principal) {
        User user = userRepository.findByUserId(principal.getName());
        likeService.likePost(user.getUserIndex(), postId);
    }

    @PostMapping("/dislike/{postId}")
    public void toggleDislike(@PathVariable Long postId, Principal principal) {
        User user = userRepository.findByUserId(principal.getName());
        likeService.dislikePost(user.getUserIndex(), postId);
    }

    @GetMapping("/status/{postId}")
    public Optional<Likes> getUserLikeStatus(@PathVariable Long postId, Principal principal) {
        User user = userRepository.findByUserId(principal.getName());
        return likeService.getUserLikeForPost(user.getUserIndex(), postId);
    }
}
