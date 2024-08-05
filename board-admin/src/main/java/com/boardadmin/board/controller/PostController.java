package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.Comment;
import com.boardadmin.board.model.File;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.CommentService;
import com.boardadmin.board.service.FileService;
import com.boardadmin.board.service.PostService;
import com.boardadmin.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private BoardService boardService;
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private FileService fileService;
    
    @GetMapping("/search")
    public String searchPosts(@RequestParam String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.searchPosts(keyword, pageable);
        
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "posts/searchResults";
    }

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
    
    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable Long postId, Model model) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<File> files = fileService.getFilesByPostId(postId); // 파일 목록 추가
        
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("files", files); // 파일 목록 추가
        return "posts/view";
    }

    @GetMapping("/new")
    public String createPostForm(@RequestParam Long boardId, Model model) {
        Post post = new Post();
        Board board = boardService.getBoardById(boardId).orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
        post.setBoard(board);
        model.addAttribute("post", post);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
        model.addAttribute("boards", boardService.getAllBoards());

        return "posts/create";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute Post post, @RequestParam Long boardId) {
        Board board = boardService.getBoardById(boardId).orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
        post.setBoard(board);
        postService.createPost(post);
        return "redirect:/admin/posts/board/" + boardId;
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        List<File> files = fileService.getFilesByPostId(id); // 파일 목록 추가
        
        model.addAttribute("post", post);
        model.addAttribute("files", files); // 파일 목록 추가

        Board board = post.getBoard();
        model.addAttribute("board", board);
        model.addAttribute("boards", boardService.getAllBoards());

        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        Board board = post.getBoard();
        postService.updatePost(id, post);
        return "redirect:/admin/posts/board/" + board.getBoardId();
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        Long boardId = postService.getPostById(id).map(Post::getBoard).map(Board::getBoardId).orElse(null);
        postService.deletePost(id);
        return "redirect:/admin/posts/board/" + (boardId != null ? boardId : "");
    }
}
