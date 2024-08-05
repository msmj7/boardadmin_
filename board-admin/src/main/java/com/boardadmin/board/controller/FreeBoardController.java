package com.boardadmin.board.controller;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.model.Comment;
import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.File;

import com.boardadmin.board.service.PostService;
import com.boardadmin.board.service.CommentService;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.board.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private FileService fileService; //파일첨부기능 추가

    @GetMapping
    public String getPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        page = (page < 1) ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postService.getPostsByBoardId(2L, pageable); // Assuming board ID 2 is for freeboard
        long totalPosts = postPage.getTotalElements(); // 전체 게시글 수
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages() > 0 ? postPage.getTotalPages() : 1);
        model.addAttribute("totalPosts", totalPosts); // 전체 게시글 수를 모델에 추가
        return "freeboard/list";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        List<Comment> comments = commentService.getCommentsByPostId(id);
        List<File> files = fileService.getFilesByPostId(id);//파일 추가
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("currentUserName", username);
        model.addAttribute("files", files);//파일 추가
        
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        post.setAuthorName(currentUserName);
        model.addAttribute("post", post);
        return "freeboard/create";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute Post post, @RequestParam("files") List<MultipartFile> files) {
        // Assuming board ID 2 is for freeboard
        Board board = boardService.getBoardById(2L).orElseThrow(() -> new IllegalArgumentException("Invalid board Id: 2"));
        post.setBoard(board);
        Post savedPost = postService.createPost(post);

        for (MultipartFile file : files) {
        	if (!file.isEmpty()) {
	            try {
	                fileService.storeFile(file, savedPost.getPostId());
	            } catch (IOException e) {
	                e.printStackTrace();
	                // 에러 처리 로직 추가
	            }
        	}
        }  	

        return "redirect:/freeboard";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        List<File> files = fileService.getFilesByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("files", files);
        return "freeboard/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, @RequestParam("files") List<MultipartFile> files, @RequestParam("deletedFiles") String deletedFiles) {
        // Retrieve the original post
        Post existingPost = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        // Set the board from the original post to the updated post
        post.setBoard(existingPost.getBoard());
        postService.updatePost(id, post);

        // Delete specified files
        if (!deletedFiles.isEmpty()) {
            String[] fileIds = deletedFiles.split(",");
            for (String fileId : fileIds) {
                fileService.deleteFile(Long.parseLong(fileId));
            }
        }

        // Store new files
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    fileService.storeFile(file, id);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 에러 처리 로직 추가
                }
            }
        }

        return "redirect:/freeboard/" + id;
    }

}
