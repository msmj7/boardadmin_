package com.boardadmin.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "postId")
    private Post post;

    @NotNull
    private String originalName; // 실제 파일명

    @NotNull
    private String saveName; // 저장 파일명

    @NotNull
    private String filePath; // 파일 경로

    @NotNull
    private Long size; // 파일 크기

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now(); // 등록 일자

    public String getFormattedSize() {
        if (size >= 1024 * 1024) {
            return String.format("%.2f MB", (double) size / (1024 * 1024));
        } else if (size >= 1024) {
            return String.format("%.2f KB", (double) size / 1024);
        } else {
            return size + " Bytes";
        }
    }
}
