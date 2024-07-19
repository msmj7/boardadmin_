package com.boardadmin.user.controller;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping("/admin/login")
    public String admin() {
        return "login/adminLogin";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String signUpUser(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUserId("");
            model.addAttribute("user", user);
            return "login/signup";
        }
        
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("USER"));
        user.setRoles(roles);
        user.setActive(true);
        userService.saveUser(user);
        return "redirect:/login";
    }
}
