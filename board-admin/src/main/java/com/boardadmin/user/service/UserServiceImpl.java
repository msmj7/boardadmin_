package com.boardadmin.user.service;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUserIndex(Integer userIndex) {
        return userRepository.findByUserIndex(userIndex)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserIndex()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existingUser.setEmail(user.getEmail());
        existingUser.setActive(user.isActive());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(encodePassword(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUserByUserIndex(Integer userIndex) {
        userRepository.findByUserIndex(userIndex)
                .ifPresent(user -> userRepository.deleteById(userIndex));
    }

    @Override
    @Transactional
    public User createUserWithRole(User user, String roleName) {
        Set<Role> roles = new HashSet<>();
        roles.add(getRoleByName(roleName));
        user.setRoles(roles);
        user.setPassword(encodePassword(user.getPassword()));
        return saveUser(user);
    }

    @Override
    @Transactional
    public User updateUserDetails(Integer userIndex, User updatedUser, String newPassword) {
        User user = getUserByUserIndex(userIndex);
        user.setEmail(updatedUser.getEmail());
        user.setActive(updatedUser.isActive());
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(encodePassword(newPassword));
        }
        return userRepository.save(user);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(roleName));
    }

    @Override
    public List<User> getAdmins() {
        return getAllUsers().stream()
                .filter(user -> hasRole(user, "ADMIN"))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsers() {
        return getAllUsers().stream()
                .filter(user -> hasRole(user, "USER"))
                .collect(Collectors.toList());
    }

    public String encodePassword(String rawPassword) {
        if (!rawPassword.startsWith("$2a$")) {
            return passwordEncoder.encode(rawPassword);
        }
        return rawPassword;
    }

    private List<org.springframework.security.core.authority.SimpleGrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User loadUserByUserIndex(Integer userIndex) throws UsernameNotFoundException {
        return userRepository.findByUserIndex(userIndex)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
