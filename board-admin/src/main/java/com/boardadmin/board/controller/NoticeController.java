package com.boardadmin.board.controller;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private PostService postService;

    @GetMapping
    public String getNotices(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        page = (page < 1) ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postService.getPostsByBoardId(1L, pageable); // Assuming board ID 1 is for notices
        long totalPosts = postPage.getTotalElements(); // 전체 게시글 수
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages() > 0 ? postPage.getTotalPages() : 1);
        model.addAttribute("totalPosts", totalPosts); // 전체 게시글 수를 모델에 추가
        return "notices/list";
    }


    @GetMapping("/{id}")
    public String getNotice(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        
        // 조회수 증가 처리
        postService.increaseViewCount(id, request, response);
        
        model.addAttribute("post", post);
        return "notices/view";
    }
}
