package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    List<User> getAllUsers();
    User getUserByUserId(String userId);
    User getUserByUserIndex(Integer userIndex);
    User saveUser(User user);
    User updateUser(User user);
    void deleteUserByUserIndex(Integer userIndex);
    boolean hasRole(User user, String roleName);
    Role getRoleByName(String roleName);
    boolean userExists(String userId);
    Page<User> getUsersByRole(String role, Pageable pageable);
	Page<User> searchAdmins(String search, Pageable pageable);
	Page<User> searchUsers(String search, Pageable pageable);
}
