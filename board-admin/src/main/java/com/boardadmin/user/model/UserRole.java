package com.boardadmin.user.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ur_index;

    @ManyToOne
    @JoinColumn(name = "user_index", referencedColumnName = "userIndex")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "roleId")
    private Role role;
}
