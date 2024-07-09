package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    List<User> getAllUsers();
    User getUserByUserIndex(Integer userIndex);
    User saveUser(User user);
    User updateUser(User user);
    void deleteUserByUserIndex(Integer userIndex);
    boolean hasRole(User user, String roleName);
    Role getRoleByName(String roleName);
    String encodePassword(String rawPassword);
    //controller에서 service로 옮
    List<User> getAdmins();
    List<User> getUsers();
    User createUserWithRole(User user, String roleName);
    User updateUserDetails(Integer userIndex, User user, String newPassword);
    User loadUserByUserIndex(Integer userIndex) throws UsernameNotFoundException;
}

