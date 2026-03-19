package com.insurance.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysOrg;
import com.insurance.auth.entity.SysRole;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户Controller - 提供用户管理相关的RESTful API
 */
@RestController
@RequestMapping("/api/user")
public class SysUserController {
    
    @Autowired
    private SysUserService sysUserService;
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long orgId) {
        Page<SysUser> page = sysUserService.findPage(pageNum, pageSize, keyword, orgId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<SysUser> list = sysUserService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", user != null);
        result.put("data", user);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody SysUser user) {
        SysUser existing = sysUserService.getByUsername(user.getUsername());
        if (existing != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户名已存在");
            return ResponseEntity.ok(result);
        }
        SysUser created = sysUserService.create(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        boolean success = sysUserService.update(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = sysUserService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        boolean success = sysUserService.updateStatus(id, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable Long id, 
            @RequestParam String newPassword) {
        boolean success = sysUserService.resetPassword(id, newPassword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/org/{orgId}")
    public ResponseEntity<Map<String, Object>> getOrg(@PathVariable Long orgId) {
        SysOrg org = sysUserService.getOrgById(orgId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", org != null);
        result.put("data", org);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable Long roleId) {
        SysRole role = sysUserService.getRoleById(roleId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", role != null);
        result.put("data", role);
        return ResponseEntity.ok(result);
    }
}
