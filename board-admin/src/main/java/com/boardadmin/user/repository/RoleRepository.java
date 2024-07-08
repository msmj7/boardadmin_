package com.boardadmin.user.repository;

import com.boardadmin.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleName(String roleName);
}
