package com.insurance.underwriting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.underwriting.entity.Underwriting;
import com.insurance.underwriting.service.UnderwritingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/underwriting")
@RequiredArgsConstructor
public class UnderwritingController {
    
    private final UnderwritingService underwritingService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam Long applicationId,
            @RequestParam String applicationNo) {
        Underwriting uw = underwritingService.createUnderwriting(applicationId, applicationNo);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", uw);
        response.put("message", "核保创建成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestBody(required = false) Map<String, Object> params) {
        int pageNum = params != null && params.get("pageNum") != null 
            ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params != null && params.get("pageSize") != null 
            ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String result = params != null ? (String) params.get("result") : null;
        
        IPage<Underwriting> page = underwritingService.getPage(pageNum, pageSize, result);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Underwriting uw = underwritingService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (uw != null) {
            response.put("code", 200);
            response.put("data", uw);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "核保记录不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Map<String, Object>> getByApplication(@PathVariable Long applicationId) {
        Underwriting uw = underwritingService.getByApplicationId(applicationId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", uw);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/no/{applicationNo}")
    public ResponseEntity<Map<String, Object>> getByNo(@PathVariable String applicationNo) {
        var list = underwritingService.getByApplicationNo(applicationNo);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 核保通过
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id,
            @RequestParam(defaultValue = "A") String level,
            @RequestParam(defaultValue = "核保通过") String opinion,
            @RequestParam(defaultValue = "system") String underwriter) {
        Underwriting uw = underwritingService.approve(id, level, opinion, underwriter);
        Map<String, Object> response = new HashMap<>();
        if (uw != null) {
            response.put("code", 200);
            response.put("data", uw);
            response.put("message", "核保通过，已自动创建保单");
        } else {
            response.put("code", 404);
            response.put("message", "核保记录不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 核保拒绝
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable Long id,
            @RequestParam(defaultValue = "核保拒绝") String opinion,
            @RequestParam(defaultValue = "system") String underwriter) {
        Underwriting uw = underwritingService.reject(id, opinion, underwriter);
        Map<String, Object> response = new HashMap<>();
        if (uw != null) {
            response.put("code", 200);
            response.put("data", uw);
            response.put("message", "核保拒绝");
        } else {
            response.put("code", 404);
            response.put("message", "核保记录不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 人工核保（兼容旧接口）
     */
    @PutMapping("/{id}/manual")
    public ResponseEntity<Map<String, Object>> manualUnderwriting(
            @PathVariable Long id,
            @RequestParam String result,
            @RequestParam(defaultValue = "A") String level,
            @RequestParam String opinion,
            @RequestParam(defaultValue = "system") String underwriter) {
        Underwriting uw = underwritingService.manualUnderwriting(id, result, level, opinion, underwriter);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", uw);
        response.put("message", "人工核保完成");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 核保结果（通用接口）
     */
    @PutMapping("/{id}/result")
    public ResponseEntity<Map<String, Object>> result(
            @PathVariable Long id,
            @RequestParam String result,
            @RequestParam(required = false) String opinion) {
        Underwriting uw;
        if ("PASSED".equals(result)) {
            uw = underwritingService.approve(id, "A", opinion != null ? opinion : "核保通过", "system");
        } else {
            uw = underwritingService.reject(id, opinion != null ? opinion : "核保拒绝", "system");
        }
        Map<String, Object> response = new HashMap<>();
        if (uw != null) {
            response.put("code", 200);
            response.put("data", uw);
            response.put("message", "PASSED".equals(result) ? "核保通过" : "核保拒绝");
        } else {
            response.put("code", 404);
            response.put("message", "核保记录不存在");
        }
        return ResponseEntity.ok(response);
    }
}
