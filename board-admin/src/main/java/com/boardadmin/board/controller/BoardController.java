package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping
    public String getAllBoards(Model model) {
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "boards/list";
    }

    @GetMapping("/new")
    public String createBoardForm(Model model) {
        model.addAttribute("board", new Board());
        model.addAttribute("boards", boardService.getAllBoards()); 
        return "boards/create";
    }

    @PostMapping
    public String createBoard(@ModelAttribute Board board) {
        boardService.createBoard(board);
        return "redirect:/boards";
    }

    @GetMapping("/{id}")
    public String getBoardById(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("boardId", id);
        // Fetch posts for the board and add pagination logic here
        // ...
        Optional<Board> board = boardService.getBoardById(id);
        if (board.isPresent()) {
            model.addAttribute("board", board.get());
            model.addAttribute("boards", boardService.getAllBoards());  // 추가
        } else {
            return "error/404"; // 존재하지 않는 게시판 ID일 경우 404 페이지로 리다이렉트합니다.
        }
        return "boards/detail"; // 이 템플릿은 board의 세부 정보를 보여주도록 합니다.
    }

    @PostMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/boards";
    }
}