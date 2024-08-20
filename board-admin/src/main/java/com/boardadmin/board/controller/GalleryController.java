package com.boardadmin.board.controller;

import com.boardadmin.board.model.File;
import com.boardadmin.board.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping
    public String getGallery(Model model) {
        // .jpg 파일만 가져오기
        List<File> jpgFiles = fileRepository.findByFilePathEndingWithOrderByCreatedDateDesc(".jpg");

        model.addAttribute("jpgFiles", jpgFiles);
        return "gallery/list";
    }
}
