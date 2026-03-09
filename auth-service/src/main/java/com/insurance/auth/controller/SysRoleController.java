package com.insurance.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysPermission;
import com.insurance.auth.entity.SysRole;
import com.insurance.auth.service.SysRoleService;
import com.insurance.auth.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    
    @Autowired
    private SysRoleService sysRoleService;
    
    @Autowired
    private UserPermissionService userPermissionService;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<SysRole> list = sysRoleService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysRole> page = sysRoleService.findPage(pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", role != null);
        result.put("data", role);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> getRolePermissions(@PathVariable Long id) {
        List<SysPermission> permissions = userPermissionService.getPermissionsByRoleId(id);
        List<Long> permIds = permissions.stream()
            .map(SysPermission::getId)
            .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", permIds);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/assignPermission")
    public ResponseEntity<Map<String, Object>> assignPermission(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        List<Long> permissionIds = ((List<?>) params.get("permissionIds")).stream()
            .map(id -> Long.valueOf(id.toString()))
            .collect(Collectors.toList());
        
        userPermissionService.assignPermissions(roleId, permissionIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody SysRole role) {
        SysRole created = sysRoleService.create(role);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        boolean success = sysRoleService.update(role);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = sysRoleService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
