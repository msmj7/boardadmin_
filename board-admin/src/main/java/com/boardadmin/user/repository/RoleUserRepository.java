package com.boardadmin.user.repository;

import com.boardadmin.user.model.UserRole;
import com.boardadmin.user.model.User;
import com.boardadmin.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleUserRepository extends JpaRepository<UserRole, Integer> {
    UserRole findByUserAndRole(User user, Role role);
}
