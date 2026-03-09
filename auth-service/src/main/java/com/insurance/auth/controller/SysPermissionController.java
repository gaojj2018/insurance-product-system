package com.insurance.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysPermission;
import com.insurance.auth.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permission")
public class SysPermissionController {
    
    @Autowired
    private SysPermissionService sysPermissionService;
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SysPermission> page = sysPermissionService.findPage(pageNum, pageSize, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<SysPermission> list = sysPermissionService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> findTree() {
        List<SysPermission> tree = sysPermissionService.findTree();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", tree);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        SysPermission permission = sysPermissionService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", permission != null);
        result.put("data", permission);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody SysPermission permission) {
        SysPermission created = sysPermissionService.create(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        boolean success = sysPermissionService.update(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = sysPermissionService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "该权限下存在子权限，无法删除");
        }
        return ResponseEntity.ok(result);
    }
}
