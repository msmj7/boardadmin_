package com.boardadmin.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @Column(nullable = false)
    private String content;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        createdAt = LocalDateTime.now();
    }

    public void setPostId(Long postId) {
        if (this.post == null) {
            this.post = new Post();
        }
        this.post.setPostId(postId);
    }
}
