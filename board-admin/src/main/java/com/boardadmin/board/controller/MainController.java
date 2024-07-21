package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.board.service.PostService;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String mainPage(Model model) {
        List<Board> boards = boardService.getAllBoards();
        List<Post> recentPosts = postService.getRecentPosts();
                
        model.addAttribute("boards", boards);
        model.addAttribute("recentPosts", recentPosts);
        
        
        return "main/main";
    }

    @GetMapping("/mypage")
    public String myPage(Model model) {
        String currentUserId = getCurrentUserId();
        User user = userService.getUserByUserId(currentUserId);
        model.addAttribute("user", user);
        return "main/myPage";
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
