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
import org.springframework.data.domain.PageRequest;
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
        List<Post> noticePosts = postService.getPostsByBoardId(1L, PageRequest.of(0, 6)).getContent(); // 공지사항 6개 가져오기
        
        // 분할해서 모델에 추가
        if (noticePosts.size() > 0) model.addAttribute("noticePosts1", noticePosts.subList(0, Math.min(2, noticePosts.size())));
        if (noticePosts.size() > 2) model.addAttribute("noticePosts2", noticePosts.subList(2, Math.min(4, noticePosts.size())));
        if (noticePosts.size() > 4) model.addAttribute("noticePosts3", noticePosts.subList(4, Math.min(6, noticePosts.size())));
        
        model.addAttribute("noticePosts", noticePosts);               
        model.addAttribute("boards", boards);
        model.addAttribute("recentPosts", recentPosts);
        
        // 현재 사용자 정보 추가
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            User currentUser = userService.getUserByUserId(currentUserId);
            model.addAttribute("currentUser", currentUser);
        }
        
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
