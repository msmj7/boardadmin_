package com.boardadmin.board.controller;

import com.boardadmin.board.model.Likes;
import com.boardadmin.board.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes/{postId}")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestParam Integer userIndex) {
        likeService.addLike(postId, userIndex);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestParam Integer userIndex) {
        likeService.removeLike(postId, userIndex);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long postId, @RequestParam Integer userIndex) {
        boolean hasLiked = likeService.hasLiked(postId, userIndex);
        return ResponseEntity.ok(hasLiked);
    }
}
