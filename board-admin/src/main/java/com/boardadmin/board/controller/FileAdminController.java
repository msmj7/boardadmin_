package com.boardadmin.board.controller;

import com.boardadmin.board.model.File;
import com.boardadmin.board.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/files")
public class FileAdminController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public String listFiles(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<File> filePage = fileService.getAllFiles(pageable);
        
        int totalPages = filePage.getTotalPages() > 0 ? filePage.getTotalPages() : 1;

        model.addAttribute("files", filePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        return "files/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return "redirect:/admin/files";
    }

    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable Long postId) {
        return "redirect:/admin/posts/view/" + postId;
    }
}
