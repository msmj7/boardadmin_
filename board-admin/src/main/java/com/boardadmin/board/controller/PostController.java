package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.PostService;
import com.boardadmin.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/posts")
public class PostController {

    

    @Autowired
    private PostService postService;

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/{boardId}")
    public String getPostsByBoardId(@PathVariable Long boardId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostsByBoardId(boardId, pageable);
        
        int totalPages = postPage.getTotalPages() > 0 ? postPage.getTotalPages() : 1; // 최소 1페이지를 유지
        
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("boardId", boardId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        // Board 정보 추가
        Board board = boardService.getBoardById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
        model.addAttribute("board", board);

        model.addAttribute("boards", boardService.getAllBoards());

        return "posts/list";
    }

    @GetMapping("/new")
    public String createPostForm(@RequestParam Long boardId, Model model) {
        Post post = new Post();
        post.setBoardId(boardId);
        model.addAttribute("post", post);
        model.addAttribute("boardId", boardId);

        // Board 정보 추가
        Board board = boardService.getBoardById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
        model.addAttribute("board", board);

        model.addAttribute("boards", boardService.getAllBoards());

        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post) {
        postService.createPost(post);
        return "redirect:/posts/board/" + post.getBoardId();
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        model.addAttribute("post", post);

        // Board 정보 추가
        Board board = boardService.getBoardById(post.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + post.getBoardId()));
        model.addAttribute("board", board);

        model.addAttribute("boards", boardService.getAllBoards());

        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        postService.updatePost(id, post);
        return "redirect:/posts/board/" + post.getBoardId();
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        Long boardId = postService.getPostById(id).map(Post::getBoardId).orElse(null);
        postService.deletePost(id);
        return "redirect:/posts/board/" + (boardId != null ? boardId : "");
    }
}
