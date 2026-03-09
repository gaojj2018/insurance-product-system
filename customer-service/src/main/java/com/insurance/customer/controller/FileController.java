package com.insurance.customer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "general") String type) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件不能为空");
            return ResponseEntity.ok(result);
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            
            File uploadDir = new File(uploadPath + File.separator + type);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            Path filePath = Paths.get(uploadPath, type, newFilename);
            Files.write(filePath, file.getBytes());
            
            String fileUrl = "/api/file/" + type + "/" + newFilename;
            
            result.put("success", true);
            result.put("fileName", newFilename);
            result.put("originalName", originalFilename);
            result.put("fileUrl", fileUrl);
            result.put("fileSize", file.getSize());
            
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{type}/{filename}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath, type, filename);
            byte[] data = Files.readAllBytes(filePath);
            
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=" + filename)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{type}/{filename}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String type,
            @PathVariable String filename) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Path filePath = Paths.get(uploadPath, type, filename);
            Files.deleteIfExists(filePath);
            
            result.put("success", true);
            result.put("message", "文件删除成功");
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件删除失败");
        }
        
        return ResponseEntity.ok(result);
    }
}
