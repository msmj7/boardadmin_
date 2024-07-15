package com.boardadmin.board.service;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    public Optional<Board> updateBoard(Long id, Board boardDetails) {
        return boardRepository.findById(id).map(board -> {
            board.setName(boardDetails.getName());
            board.setDescription(boardDetails.getDescription());
            return boardRepository.save(board);
        });
    }

    public boolean deleteBoard(Long id) {
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
