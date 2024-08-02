package com.boardadmin.board.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.model.File;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.board.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileService fileService;

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
        return "redirect:/admin/boards";
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

    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/admin/boards";
    }

    @GetMapping("/edit/{id}")
    public String editBoardForm(@PathVariable Long id, Model model) {
        Optional<Board> board = boardService.getBoardById(id);
        if (board.isPresent()) {
            model.addAttribute("board", board.get());
            return "boards/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateBoard(@PathVariable Long id, @ModelAttribute Board board) {
        boardService.updateBoard(id, board);
        return "redirect:/admin/boards";
    }

    @PostMapping("/upload/{id}")
    public String uploadFiles(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files, Model model) {
        List<File> existingFiles = fileService.getFilesByPostId(id);
        if (existingFiles.size() + files.size() > 3) {
            model.addAttribute("error", "You can upload up to 3 files.");
            return "redirect:/admin/boards/edit/" + id;
        }

        for (MultipartFile file : files) {
            try {
                fileService.storeFile(file, id);
            } catch (IOException e) {
                e.printStackTrace();
                // 에러 처리 로직 추가
            }
        }
        return "redirect:/admin/boards/" + id;
    }

    @GetMapping("/files/{id}")
    public String getFiles(@PathVariable Long id, Model model) {
        List<File> files = fileService.getFilesByPostId(id);
        model.addAttribute("files", files);
        return "boards/files";
    }

    @GetMapping("/files/delete/{id}")
    public String deleteFile(@PathVariable Long id, @RequestParam("postId") Long postId) {
        fileService.deleteFile(id);
        return "redirect:/admin/boards/files/" + postId;
    }
}
