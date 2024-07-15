package com.boardadmin.board.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Board {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
}
