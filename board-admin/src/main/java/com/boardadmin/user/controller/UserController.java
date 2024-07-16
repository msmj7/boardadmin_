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

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;  
    }

    @GetMapping
    public String getAdminsPage(Model model) {

        List<User> allUsers = userService.getAllUsers();

        List<User> admins = allUsers.stream()
                .filter(user -> userService.hasRole(user, "ADMIN"))
                .collect(Collectors.toList());


        model.addAttribute("admins", admins);

        return "useradmin/admins"; 
    }

    @GetMapping("/users")
    public String getUsersPage(Model model) {

        List<User> users = userService.getAllUsers().stream()
                .filter(user -> userService.hasRole(user, "USER"))
                .collect(Collectors.toList());
        
        //logger.info("Users: " + users);
        
        model.addAttribute("users", users);

        return "useradmin/users"; 
    }
    
    @GetMapping("/admin/{userIndex}")
    public String getAdminDetailPage(@PathVariable Integer userIndex, Model model) {
        User admin = userService.getUserByUserIndex(userIndex);
        model.addAttribute("admin", admin);
        return "useradmin/adminDetail";
    }

    @GetMapping("/user/{userIndex}")
    public String getUserDetailPage(@PathVariable Integer userIndex, Model model) {
        User user = userService.getUserByUserIndex(userIndex);
        model.addAttribute("user", user);
        return "useradmin/userDetail";
    }
    
    @GetMapping("/newAdmin")
    public String getAdminCreatePage(Model model) {
        model.addAttribute("user", new User());
        return "useradmin/newAdmin";
    }

    @GetMapping("/newUser")
    public String getUserCreatePage(Model model) {
        model.addAttribute("user", new User());
        return "useradmin/newUser";
    }

    
    @PostMapping("/create/admin")
    public String createAdmin(@ModelAttribute User user, Model model) {
    	if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "User ID already exists");
            return "useradmin/create";
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("ADMIN"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/user-management";
    }

    @PostMapping("/create/user")
    public String createUser(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "User ID already exists");
            return "useradmin/create";
        }
    	
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("USER"));
        user.setRoles(roles); 
        userService.saveUser(user);
        return "redirect:/user-management/users";
    }

    @PostMapping("/update/admin/{userIndex}")
    public String updateAdmin(@PathVariable Integer userIndex, @ModelAttribute User user) {
        User existingUser = userService.getUserByUserIndex(userIndex);
        if (existingUser != null) {
        	existingUser.setUserId(user.getUserId());
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.updateUser(existingUser);
        }
        return "redirect:/user-management";
    }

    @DeleteMapping("/delete/admin/{userIndex}")
    public String deleteAdmin(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/user-management";
    }
    
    @PostMapping("/update/user/{userIndex}")
    public String updateUser(@PathVariable Integer userIndex, @ModelAttribute User user) {
        User existingUser = userService.getUserByUserIndex(userIndex);
        if (existingUser != null) {
        	existingUser.setUserId(user.getUserId());
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.updateUser(existingUser);
        }
        return "redirect:/user-management/users";
    }


    @DeleteMapping("/delete/user/{userIndex}")
    public String deleteUser(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/user-management/users";
    }


}