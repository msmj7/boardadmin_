package com.boardadmin.board.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("servletPath") // 현재 사이트 url 받아오기
    public String getRequestServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}
