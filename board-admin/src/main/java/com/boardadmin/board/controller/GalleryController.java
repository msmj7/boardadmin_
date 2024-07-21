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
                "https://picsum.photos/800/400?image=1",
                "https://picsum.photos/800/400?image=2",
                "https://picsum.photos/800/400?image=3",
                "https://picsum.photos/800/400?image=4",
                "https://picsum.photos/800/400?image=5",
                "https://picsum.photos/800/400?image=6",
                "https://picsum.photos/800/400?image=7",
                "https://picsum.photos/800/400?image=8",
                "https://picsum.photos/800/400?image=9",
                "https://picsum.photos/800/400?image=10",
                "https://picsum.photos/800/400?image=11",
                "https://picsum.photos/800/400?image=12",
                "https://picsum.photos/800/400?image=13",
                "https://picsum.photos/800/400?image=14",
                "https://picsum.photos/800/400?image=15",
                "https://picsum.photos/800/400?image=16",
                "https://picsum.photos/800/400?image=17",
                "https://picsum.photos/800/400?image=18",
                "https://picsum.photos/800/400?image=19",
                "https://picsum.photos/800/400?image=20"
        );

        model.addAttribute("images", images);
        return "gallery/list";
    }
}
