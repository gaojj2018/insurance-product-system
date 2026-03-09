package com.insurance.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysOrg;
import com.insurance.auth.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/org")
public class SysOrgController {
    
    @Autowired
    private SysOrgService sysOrgService;
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SysOrg> page = sysOrgService.findPage(pageNum, pageSize, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<SysOrg> list = sysOrgService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> findTree() {
        List<SysOrg> tree = sysOrgService.findTree();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", tree);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        SysOrg org = sysOrgService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", org != null);
        result.put("data", org);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody SysOrg org) {
        SysOrg created = sysOrgService.create(org);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody SysOrg org) {
        org.setId(id);
        boolean success = sysOrgService.update(org);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = sysOrgService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "该机构下存在子机构，无法删除");
        }
        return ResponseEntity.ok(result);
    }
}
