package com.boardadmin.user.repository;

import com.boardadmin.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    
	User findByUserId(String userId);
    
 // 모든 사용자 조회
    List<User> findAll();
    
    Page<User> findByRolesRoleName(String role, Pageable pageable);

    //검색 기능 추가
 // 검색 기능 추가
    Page<User> findByUserIdContainingOrEmailContainingAndRolesRoleName(String userId, String email, String role, Pageable pageable);

    User findByEmail(String email);
}
