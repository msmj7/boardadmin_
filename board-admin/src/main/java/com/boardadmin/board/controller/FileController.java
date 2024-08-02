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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        File file = fileService.getFileById(id);
        Path filePath = Paths.get(file.getFilePath()).toAbsolutePath().normalize();
        
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("File not found: " + filePath);
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String encodedFileName = encodeFileName(file.getOriginalName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, @RequestParam("postId") Long postId) {
        fileService.deleteFile(id);
        return "redirect:/freeboard/edit/" + postId;
    }

    //파일명 인코딩 추가	
    private String encodeFileName(String fileName) { 
        try {
            return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode file name: " + fileName, e);
        }
    }
}
