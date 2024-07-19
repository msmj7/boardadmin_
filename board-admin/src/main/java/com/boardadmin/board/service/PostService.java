package com.boardadmin.board.service;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    public Page<Post> getPostsByBoardId(Long boardId, Pageable pageable) {
        return postRepository.findByBoard_BoardId(boardId, pageable);
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void updatePost(Long postId, Post post) {
        post.setPostId(postId);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
    
   

    public List<Post> getRecentPosts() {
        Pageable pageable = PageRequest.of(0, 5); // 최근 게시글 5개 가져오기
        return postRepository.findByOrderByCreatedAtDesc(pageable).getContent();
    }

    
}
