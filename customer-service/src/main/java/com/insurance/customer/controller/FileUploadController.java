package com.insurance.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/customer/file")
public class FileUploadController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final String[] ALLOWED_TYPES = {
        "image/jpeg", "image/png", "image/gif", "application/pdf"
    };

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.ok(response);
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("code", 400);
            response.put("message", "文件大小不能超过10MB");
            return ResponseEntity.ok(response);
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!isAllowedType(contentType)) {
            response.put("code", 400);
            response.put("message", "不支持的文件类型，仅支持jpg/png/gif/pdf");
            return ResponseEntity.ok(response);
        }
        
        try {
            // 创建上传目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path dirPath = Paths.get(uploadPath, type, dateDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = dirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            // 返回文件信息
            String fileUrl = "/api/customer/file/download/" + type + "/" + dateDir + "/" + newFilename;
            
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", newFilename);
            data.put("originalName", originalFilename);
            data.put("fileUrl", fileUrl);
            data.put("fileSize", file.getSize());
            data.put("contentType", contentType);
            
            log.info("文件上传成功: originalName={}, savedName={}, size={}", 
                    originalFilename, newFilename, file.getSize());
            
            response.put("code", 200);
            response.put("data", data);
            response.put("message", "上传成功");
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{type}/{year}/{month}/{day}/{filename}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String type,
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadPath, type, year, month, day, filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] content = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(content);
                    
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isAllowedType(String contentType) {
        if (contentType == null) return false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
    
    @PostMapping("/idCard")
    public ResponseEntity<Map<String, Object>> uploadIdCard(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "idcard");
    }
    
    @PostMapping("/healthDeclaration")
    public ResponseEntity<Map<String, Object>> uploadHealthDeclaration(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "health");
    }
}
