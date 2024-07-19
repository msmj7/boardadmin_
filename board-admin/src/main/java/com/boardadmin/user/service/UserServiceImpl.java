package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

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
        return userRepository.findAll();
    }

    @Override
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public User getUserByUserIndex(Integer userIndex) {
        return userRepository.findById(userIndex)
                .orElseThrow(() -> new IllegalArgumentException("User not found for index: " + userIndex));
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUserByUserIndex(Integer userIndex) {
        userRepository.deleteById(userIndex);
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
    public boolean updateUser(Integer userIndex, User user) {
        User existingUser = userRepository.findById(userIndex)
                .orElseThrow(() -> new IllegalArgumentException("User not found for index: " + userIndex));

        if (!existingUser.getUserId().equals(user.getUserId()) && userExists(user.getUserId())) {
            return false;
        } else {
            existingUser.setUserId(user.getUserId());
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(existingUser);
            return true;
        }
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

    @Override
    public Page<User> getAdminsPage(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (search != null && !search.isEmpty()) {
            return searchAdmins(search, pageable);
        }
        return getUsersByRole("ADMIN", pageable);
    }

    @Override
    public Page<User> getUsersPage(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (search != null && !search.isEmpty()) {
            return searchUsers(search, pageable);
        }
        return getUsersByRole("USER", pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        if (user == null || !user.isActive()) {
            throw new UsernameNotFoundException("User not found or not active");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), getAuthorities(user));
    }

    private List<org.springframework.security.core.authority.SimpleGrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());
    }
}
