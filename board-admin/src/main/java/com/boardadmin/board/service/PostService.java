package com.boardadmin.board.service;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByBoardId(Long boardId) {
        return postRepository.findByBoardId(boardId);
    }

    public void createPost(Post post) {
        postRepository.save(post);
    }

    public void updatePost(Long id, Post post) {
        if (postRepository.existsById(id)) {
            post.setPostId(id);
            postRepository.save(post);
        }
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}