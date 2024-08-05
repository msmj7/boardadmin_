package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserByUserId(String userId);

    User getUserByUserIndex(Integer userIndex);

    User saveUser(User user);

    void deleteUserByUserIndex(Integer userIndex);

    boolean hasRole(User user, String roleName);

    Role getRoleByName(String roleName);

    User updateUser(User user);

    Page<User> getUsersByRole(String role, Pageable pageable);

    boolean userExists(String userId);
    
    boolean emailExists(String email);

    Page<User> searchUsers(String search, Pageable pageable);

    Page<User> searchAdmins(String search, Pageable pageable);

    Page<User> getAdminsPage(int page, int size, String search);

    Page<User> getUsersPage(int page, int size, String search);

    boolean updateUser(Integer userIndex, User user);

    User getUserByEmail(String email);

    void assignRole(User user, String roleName);

    boolean checkPassword(User user, String rawPassword);

    void initializeRoles();

    void updateUserPassword(User user, String newPassword);
}
