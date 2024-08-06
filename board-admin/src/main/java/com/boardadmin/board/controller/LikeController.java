package com.boardadmin.board.controller;

import com.boardadmin.board.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}")
    public void likePost(@PathVariable Long postId, @RequestParam String userId) {
        likeService.likePost(postId, userId);
    }

    @DeleteMapping("/{postId}")
    public void unlikePost(@PathVariable Long postId, @RequestParam String userId) {
        likeService.unlikePost(postId, userId);
    }

    @GetMapping("/{postId}")
    public boolean isPostLikedByUser(@PathVariable Long postId, @RequestParam String userId) {
        return likeService.isPostLikedByUser(postId, userId);
    }

    @GetMapping("/count/{postId}")
    public int countLikes(@PathVariable Long postId) {
        return likeService.countLikes(postId);
    }
}
