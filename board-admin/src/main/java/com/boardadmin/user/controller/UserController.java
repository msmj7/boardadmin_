package com.boardadmin.user.controller;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/user-management")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;  
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getUserManagePage(Model model) {
        //logger.info("getUserManagePage called");

        List<User> allUsers = userService.getAllUsers();
        //logger.info("All Users: " + allUsers);

        List<User> admins = allUsers.stream()
                .filter(user -> userService.hasRole(user, "ADMIN"))
                .collect(Collectors.toList());

        //logger.info("Admins: " + admins);

        model.addAttribute("admins", admins);

        return "userManage"; 
    }

    @GetMapping("/users")
    public String getUsersPage(Model model) {
        //logger.info("getUsersPage called");

        List<User> users = userService.getAllUsers().stream()
                .filter(user -> userService.hasRole(user, "USER"))
                .collect(Collectors.toList());
        
        //logger.info("Users: " + users);
        
        model.addAttribute("users", users);

        return "user"; 
    }

    @PostMapping("/create/admin")
    public String createAdmin(User user) {
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("ADMIN"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/user-management";
    }

    @PostMapping("/create/user")
    public String createUser(User user) {
    	
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("USER"));
        user.setRoles(roles);
        
        userService.saveUser(user);
        return "redirect:/user-management/users";
    }

    @PostMapping("/update/{userIndex}")
    public String updateUser(@PathVariable Integer userIndex, User user, @RequestParam(required = false) String newPassword) {
        User existingUser = userService.getUserByUserId(user.getUserId());
        if (existingUser != null) {
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());

            // 비밀번호를 변경할 때만 업데이트
            if (newPassword != null && !newPassword.isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newPassword));
            }

            userService.updateUser(existingUser);
        }
        return "redirect:/user-management";
    }

    @DeleteMapping("/delete/{userIndex}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userIndex) {
        logger.info("Delete request received for userIndex: {}", userIndex);
        userService.deleteUserByUserIndex(userIndex);
        return ResponseEntity.ok("User deleted successfully");
    }


}