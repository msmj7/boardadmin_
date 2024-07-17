package com.boardadmin.board.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("servletPath")
    public String getRequestServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}
