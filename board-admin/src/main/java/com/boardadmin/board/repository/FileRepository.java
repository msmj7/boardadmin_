package com.boardadmin.board.repository;

import com.boardadmin.board.model.File;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByPost_PostId(Long postId);
    
    @Modifying
    @Transactional
    void deleteByPost_PostId(Long postId);
}
