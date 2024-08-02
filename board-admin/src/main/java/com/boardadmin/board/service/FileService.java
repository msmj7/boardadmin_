package com.boardadmin.board.service;

import com.boardadmin.board.model.File;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.repository.FileRepository;
import com.boardadmin.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PostRepository postRepository;

    private final String uploadDir = "C:\\sideproject\\boardadmin\\file"; // 업로드 디렉토리 경로 설정

    public File storeFile(MultipartFile file, Long postId) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String filePath = uploadDir + "/" + storedFileName;

        Path destPath = Paths.get(filePath);
        Files.copy(file.getInputStream(), destPath);

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        File fileEntity = new File();
        fileEntity.setOriginalName(originalFileName);
        fileEntity.setSaveName(storedFileName);
        fileEntity.setFilePath(filePath);
        fileEntity.setSize(file.getSize());
        fileEntity.setPost(post);

        return fileRepository.save(fileEntity);
    }

    public List<File> getFilesByPostId(Long postId) {
        return fileRepository.findByPost_PostId(postId);
    }

    public File getFileById(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + fileId));
    }

    public void deleteFile(Long fileId) {
        File fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + fileId));
        Path filePath = Paths.get(fileEntity.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileRepository.delete(fileEntity);
    }
}
