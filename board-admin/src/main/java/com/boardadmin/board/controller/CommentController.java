package com.boardadmin.board.controller;

import com.boardadmin.board.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.CommentService;
import com.boardadmin.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @GetMapping("/search")
    public String searchComments(@RequestParam String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> commentPage = commentService.searchComments(keyword, pageable);

        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "comments/searchResults";
    }

    @GetMapping
    public String getAllComments(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> commentPage = commentService.getAllComments(pageable);

        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());
        return "comments/list";
    }

    @GetMapping("/post/{postId}")
    public String getCommentsByPostId(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentService.getCommentsByPostIdOrderByCreatedAtDesc(postId); // 최신순으로 가져오기
        model.addAttribute("comments", comments);
        model.addAttribute("postId", postId);
        return "comments/list";
    }

    @PostMapping("/post/{postId}")
    public String createComment(@PathVariable Long postId, @RequestParam String content, @RequestParam String authorName) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setAuthorName(authorName); // 설정
        commentService.createComment(comment);
        return "redirect:/admin/posts/view/" + postId;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/admin/posts/view/" + postId;
    }

    // 댓글 수정 폼으로 이동하는 메서드
    @GetMapping("/edit/{id}")
    public String editCommentForm(@PathVariable Long id, Model model) {
        Comment comment = commentService.getCommentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        model.addAttribute("comment", comment);
        return "comments/edit"; // comments/edit.html 템플릿으로 이동
    }

    // 댓글 수정 요청을 처리하는 메서드
    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable Long id, @RequestParam String content, @RequestParam Long postId) {
        Comment existingComment = commentService.getCommentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        existingComment.setContent(content);
        commentService.updateComment(existingComment);
        return "redirect:/admin/posts/view/" + postId;
    }
}
