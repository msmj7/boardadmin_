package com.boardadmin.board.service;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.PostRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.CookieGenerator;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Arrays;
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

    
    //조회수 
    public void increaseViewCount(Long postId, HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 조회 여부 확인
        Cookie[] cookies = request.getCookies();
        boolean hasViewed = false;
        String postViewKey = "postView_" + postId;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(postViewKey)) {
                    hasViewed = true;
                    break;
                }
            }
        }

        if (!hasViewed) {
            // 조회수 증가
            postRepository.findById(postId).ifPresent(post -> {
                post.setViews(post.getViews() + 1);
                postRepository.save(post);

                // 조회 쿠키 추가
                Cookie viewCookie = new Cookie(postViewKey, "true");
                viewCookie.setMaxAge(24 * 60 * 60); // 1일 동안 유효
                viewCookie.setPath("/");
                response.addCookie(viewCookie);
            });
        }
    }
    
}
