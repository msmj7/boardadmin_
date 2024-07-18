package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;
import com.boardadmin.common.util.PasswordUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    //private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId);
        //if (user != null) {
            //logger.info("Fetched User: {}", user);
        //}
        return user;
    }
    
    @Override
    public User getUserByUserIndex(Integer userIndex) {
        return userRepository.findById(userIndex)
            .orElseThrow(() -> new IllegalArgumentException("User not found for index: " + userIndex));
    }


    @Override
    public User saveUser(User user) {
        //user.setPassword(PasswordUtil.encodePassword(passwordEncoder, user.getPassword())); // Password encoding
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	return userRepository.save(user);
    }
    
    @Override
    public void deleteUserByUserIndex(Integer userIndex) {
        User user = userRepository.findById(userIndex).orElse(null);
        //if (user != null) {
            //logger.info("Deleting user: {}", user);
            //if (user.getPassword() == null) {
                //logger.warn("User password is null for user: {}", user);
            //}
            userRepository.deleteById(userIndex);
        //} else {
            //logger.warn("User not found for userIndex: {}", userIndex);
        //}
    }


    @Override
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(roleName));
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserIndex())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setEmail(user.getEmail());
        existingUser.setActive(user.isActive());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }


    
    
    
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        if (user == null || !user.isActive() || !hasRole(user, "ADMIN")) {
            throw new UsernameNotFoundException("User not found or not active or not an admin");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), getAuthorities(user));
    }

    private List<org.springframework.security.core.authority.SimpleGrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> {
                    return new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getRoleName());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<User> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findByRolesRoleName(role, pageable);
    }
    
    @Override
    public boolean userExists(String userId) {
        return userRepository.findByUserId(userId) != null;
    }

    
    @Override
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.findByUserIdContainingOrEmailContainingAndRolesRoleName(search, search, "USER", pageable);
    }

    @Override
    public Page<User> searchAdmins(String search, Pageable pageable) {
        return userRepository.findByUserIdContainingOrEmailContainingAndRolesRoleName(search, search, "ADMIN", pageable);
    }

    	
}
