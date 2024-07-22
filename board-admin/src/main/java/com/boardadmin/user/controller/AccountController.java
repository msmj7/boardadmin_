package com.boardadmin.user.controller;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.EmailService;
import com.boardadmin.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
public class AccountController {

    private final UserService userService;
    private final EmailService emailService;

    public AccountController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
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
        return "login/signUp";
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

    @GetMapping("/password/reset")
    public String showPasswordResetForm() {
    	
        return "login/findPw";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", "등록된 이메일이 없습니다.");
            return "login/findPw";
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(tempPassword); // 임시 비밀번호 설정
        userService.updateUser(user);

        String subject = "임시 비밀번호 안내";
        String text = "귀하의 임시 비밀번호는 " + tempPassword + " 입니다. 로그인 후 비밀번호를 변경해주세요.";

        emailService.sendSimpleMessage(user.getEmail(), subject, text);

        model.addAttribute("success", "임시 비밀번호가 이메일로 전송되었습니다.");
        return "login/findPw";
    }

    @GetMapping("/findId")
    public String showFindIdForm() {
        return "login/findId";
    }

    @PostMapping("/findId")
    public String findId(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", "등록된 이메일이 없습니다.");
            return "login/findId";
        }

        String userId = user.getUserId();
        String maskedId = userId.substring(0, userId.length() -2) + "**";

        model.addAttribute("email", email);
        model.addAttribute("maskedId", maskedId);
        model.addAttribute("idFound", true);

        return "login/findId";
    }

    @PostMapping("/sendIdEmail")
    public String sendIdEmail(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", "등록된 이메일이 없습니다.");
            return "login/findId";
        }

        String subject = "아이디 찾기 안내";
        String text = "귀하의 아이디는 " + user.getUserId() + " 입니다.";

        emailService.sendSimpleMessage(email, subject, text);

        model.addAttribute("success", "아이디가 이메일로 전송되었습니다.");
        return "login/findId";
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
