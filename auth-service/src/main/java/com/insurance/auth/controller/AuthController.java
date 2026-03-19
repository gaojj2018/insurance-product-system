package com.insurance.auth.controller;

import com.insurance.auth.config.JwtTokenProvider;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.service.AuthService;
import com.insurance.auth.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证授权Controller - 提供登录认证和权限相关的RESTful API
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserPermissionService userPermissionService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> result = authService.login(username, password);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            List<String> permissions = jwtTokenProvider.getPermissionsFromToken(token);
            
            SysUser user = authService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                    .eq("username", username)
            );
            
            result.put("success", true);
            if (user != null) {
                result.put("userId", user.getId());
                result.put("username", user.getUsername());
                result.put("realName", user.getRealName());
                result.put("role", user.getRole());
                result.put("orgId", user.getOrgId());
            }
            result.put("permissions", permissions);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/permissions")
    public ResponseEntity<Map<String, Object>> getPermissions(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            List<String> permissions = jwtTokenProvider.getPermissionsFromToken(token);
            
            result.put("success", true);
            result.put("permissions", permissions);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SysUser user) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysUser created = authService.register(user);
            result.put("success", true);
            result.put("data", created);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        boolean valid = authService.validateToken(token);
        result.put("valid", valid);
        
        return ResponseEntity.ok(result);
    }
}
