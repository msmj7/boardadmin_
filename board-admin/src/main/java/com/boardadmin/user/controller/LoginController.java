package com.boardadmin.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String main() {
        return "main";
    }
	
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }
    
    @GetMapping("/admin/login")
    public String admin() {
        return "adminLogin"; 
    }

}

