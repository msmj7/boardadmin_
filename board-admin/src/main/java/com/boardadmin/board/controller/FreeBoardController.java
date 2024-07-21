package com.boardadmin.board.controller;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.model.Comment;
import com.boardadmin.board.model.Board;
import com.boardadmin.board.service.PostService;
import com.boardadmin.board.service.CommentService;
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
@RequestMapping("/freeboard")
public class FreeBoardController {

    @Autowired
    private PostService postService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public String getPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        page = (page < 1) ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostsByBoardId(2L, pageable); // Assuming board ID 2 is for freeboard
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages() > 0 ? postPage.getTotalPages() : 1);
        return "freeboard/list";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        List<Comment> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "freeboard/view";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam String content, @RequestParam String authorName) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setAuthorName(authorName);
        commentService.createComment(comment);
        return "redirect:/freeboard/" + id;
    }

    @GetMapping("/new")
    public String createPostForm(Model model) {
        Post post = new Post();
        model.addAttribute("post", post);
        return "freeboard/create";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute Post post) {
        // Assuming board ID 2 is for freeboard
        Board board = boardService.getBoardById(2L).orElseThrow(() -> new IllegalArgumentException("Invalid board Id: 2"));
        post.setBoard(board);
        postService.createPost(post);
        return "redirect:/freeboard";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        model.addAttribute("post", post);
        return "freeboard/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        // Retrieve the original post
        Post existingPost = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        // Set the board from the original post to the updated post
        post.setBoard(existingPost.getBoard());
        postService.updatePost(id, post);
        return "redirect:/freeboard/" + id;
    }
}
