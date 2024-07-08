package com.boardadmin.user.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    private String roleName;
    
}
