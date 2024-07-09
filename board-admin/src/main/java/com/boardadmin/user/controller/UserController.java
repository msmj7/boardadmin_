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

    //private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserManagePage(Model model) {
        model.addAttribute("admins", userService.getAdmins());
        return "userManage";
    }

    @GetMapping("/users")
    public String getUsersPage(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "user";
    }

    @PostMapping("/create/admin")
    public String createAdmin(User user) {
        userService.createUserWithRole(user, "ADMIN");
        return "redirect:/user-management";
    }

    @PostMapping("/create/user")
    public String createUser(User user) {
        userService.createUserWithRole(user, "USER");
        return "redirect:/user-management/users";
    }

    @PostMapping("/update/{userIndex}")
    public String updateUser(@PathVariable Integer userIndex, User user, @RequestParam(required = false) String newPassword) {
        userService.updateUserDetails(userIndex, user, newPassword);
        return "redirect:/user-management";
    }

    @DeleteMapping("/delete/{userIndex}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return ResponseEntity.ok("User deleted successfully");
    }


}