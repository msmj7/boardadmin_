package com.boardadmin.user.controller;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
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
    
    @GetMapping("/account/update")
    public String showUpdateForm(Model model) {
        String currentUserId = getCurrentUserId();
        User user = userService.getUserByUserId(currentUserId);
        model.addAttribute("user", user);
        return "main/update";
    }
    
    @PostMapping("/account/update")
    public String updateAccount(@ModelAttribute User user, Model model) {
        String currentUserId = getCurrentUserId();
        User existingUser = userService.getUserByUserId(currentUserId);

        if (existingUser == null) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "main/update";
        }

        user.setUserIndex(existingUser.getUserIndex());
        user.setActive(true);
        return updateUser(existingUser.getUserIndex(), user, model, "main/update", "redirect:/mypage");
    }

    @GetMapping("/account/delete")
    public String showDeleteForm(Model model) {
        String currentUserId = getCurrentUserId();
        User user = userService.getUserByUserId(currentUserId);
        model.addAttribute("user", user);
        return "main/delete";
    }
    
    @PostMapping("/account/delete")
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        String currentUserId = getCurrentUserId();
        User user = userService.getUserByUserId(currentUserId);
        user.setActive(false);
        userService.saveUser(user);
        
        // 로그아웃 처리
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        return "redirect:/";
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    protected String updateUser(Integer userIndex, User user, Model model, String errorView, String successRedirect) {
        if (!userService.updateUser(userIndex, user)) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            model.addAttribute("user", user);
            return errorView;
        }
        return successRedirect;
    }
}
