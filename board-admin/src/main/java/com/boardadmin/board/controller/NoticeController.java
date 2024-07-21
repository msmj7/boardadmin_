package com.boardadmin.board.controller;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String getNotices(@RequestParam(defaultValue = "1", required = false) int page, 
                             @RequestParam(defaultValue = "10", required = false) int size, 
                             Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostsByBoardId(1L, pageable); // Assuming board ID 1 is for notices
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("size", size);
        return "notices/list";
    }

    @GetMapping("/{id}")
    public String getNotice(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        model.addAttribute("post", post);
        return "notices/view";
    }
}
