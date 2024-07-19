package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
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
		return "main/myPage";
	}
}
