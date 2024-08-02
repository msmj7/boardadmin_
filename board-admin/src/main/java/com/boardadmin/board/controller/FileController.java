package com.boardadmin.board.controller;

import com.boardadmin.board.model.File;
import com.boardadmin.board.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") List<MultipartFile> files, @RequestParam("postId") Long postId, Model model) {
        List<File> existingFiles = fileService.getFilesByPostId(postId);
        if (existingFiles.size() + files.size() > 3) {
            model.addAttribute("error", "You can upload up to 3 files.");
            return "redirect:/freeboard/edit/" + postId;
        }
        for (MultipartFile file : files) {
            try {
                fileService.storeFile(file, postId);
            } catch (IOException e) {
                e.printStackTrace();
                // 에러 처리 로직 추가
            }
        }
        return "redirect:/freeboard/" + postId;
    }

    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        File file = fileService.getFileById(id);
        Path filePath = Paths.get(file.getFilePath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, @RequestParam("postId") Long postId) {
        fileService.deleteFile(id);
        return "redirect:/freeboard/edit/" + postId;
    }
}
