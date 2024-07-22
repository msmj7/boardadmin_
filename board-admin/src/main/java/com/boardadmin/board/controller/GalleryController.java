package com.boardadmin.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    @GetMapping
    public String getGallery(Model model) {
        // 샘플 이미지 URL 목록
        List<String> images = Arrays.asList(
                "https://picsum.photos/800/400?image=111",
                "https://picsum.photos/800/400?image=23",
                "https://picsum.photos/800/400?image=3",
                "https://picsum.photos/800/400?image=45",
                "https://picsum.photos/800/400?image=115",
                "https://picsum.photos/800/400?image=61",
                "https://picsum.photos/800/400?image=74",
                "https://picsum.photos/800/400?image=85",
                "https://picsum.photos/800/400?image=219",
                "https://picsum.photos/800/400?image=10",
                "https://picsum.photos/800/400?image=11",
                "https://picsum.photos/800/400?image=122",
                "https://picsum.photos/800/400?image=13",
                "https://picsum.photos/800/400?image=41",
                "https://picsum.photos/800/400?image=65",
                "https://picsum.photos/800/400?image=16",
                "https://picsum.photos/800/400?image=77",
                "https://picsum.photos/800/400?image=18",
                "https://picsum.photos/800/400?image=32",
                "https://picsum.photos/800/400?image=20"
        );

        model.addAttribute("images", images);
        return "gallery/list";
    }
}
