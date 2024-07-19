package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String mainPage(Model model) {
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "main/main";
    }
    
    @GetMapping("/mypage")
    public String myPage(Model model) {
        return "main/myPage";
    }
}
