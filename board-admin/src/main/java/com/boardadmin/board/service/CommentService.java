package com.boardadmin.board.service;

import com.boardadmin.board.model.Comment;
import com.boardadmin.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostPostId(postId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 페이지네이션을 지원하는 메서드 추가
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public Page<Comment> searchComments(String keyword, Pageable pageable) {
        return commentRepository.findByContentContainingIgnoreCase(keyword, pageable);
    }
    
    public List<Comment> getCommentsByPostIdOrderByCreatedAtDesc(Long postId) {
        return commentRepository.findByPostPostIdOrderByCreatedAtDesc(postId);
    }
}
