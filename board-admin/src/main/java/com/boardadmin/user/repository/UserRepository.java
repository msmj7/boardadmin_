package com.boardadmin.user.repository;

import com.boardadmin.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    
	Optional<User> findByUserIndex(Integer userIndex);
    
 // 모든 사용자 조회
    List<User> findAll();
    
}
