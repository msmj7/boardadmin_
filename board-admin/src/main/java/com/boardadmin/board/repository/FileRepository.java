package com.boardadmin.board.repository;

import com.boardadmin.board.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByPost_PostId(Long postId);
}
