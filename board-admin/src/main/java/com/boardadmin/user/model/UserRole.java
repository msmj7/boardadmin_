package com.boardadmin.user.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_role", uniqueConstraints = {
       @UniqueConstraint(columnNames = {"user_index", "role_id"})
})
public class UserRole {

    @Id
       @Column(name = "user_index")
       private Integer userIndex;

       @ManyToOne
       @MapsId
       @JoinColumn(name = "user_index", referencedColumnName = "userIndex")
       private User user;

       @ManyToOne
       @JoinColumn(name = "role_id", referencedColumnName = "roleId")
       private Role role;
}
