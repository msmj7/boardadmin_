package com.boardadmin.board.service;

import com.boardadmin.board.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.boardadmin.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    public Page<Comment> searchComments(String keyword, Pageable pageable) {
        return commentRepository.findByContentContaining(keyword, pageable);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostPostId(postId);
    }

    public void createComment(Comment comment) {
        commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment); // updateComment 메서드 구현
    }
}
