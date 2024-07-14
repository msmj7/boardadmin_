package com.boardadmin.user.controller;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    //private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;  
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getAdminsPage(Model model) {

        List<User> allUsers = userService.getAllUsers();

        List<User> admins = allUsers.stream()
                .filter(user -> userService.hasRole(user, "ADMIN"))
                .collect(Collectors.toList());


        model.addAttribute("admins", admins);

        return "admins"; 
    }

    @GetMapping("/users")
    public String getUsersPage(Model model) {

        List<User> users = userService.getAllUsers().stream()
                .filter(user -> userService.hasRole(user, "USER"))
                .collect(Collectors.toList());
        
        //logger.info("Users: " + users);
        
        model.addAttribute("users", users);

        return "users"; 
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
    public String updateUser(@PathVariable Integer userIndex, @ModelAttribute User user) {
        User existingUser = userService.getUserByUserIndex(userIndex);
        if (existingUser != null) {
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.updateUser(existingUser);
        }
        return "redirect:/user-management";
    }


    @DeleteMapping("/delete/{userIndex}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return ResponseEntity.ok("User deleted successfully");
    }


}