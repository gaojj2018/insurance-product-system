package com.insurance.application.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.application.entity.Application;
import com.insurance.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 投保申请Controller - 提供投保申请相关的RESTful API
 */
@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    /**
     * 创建投保申请（草稿状态）
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createApplication(@RequestBody Application application) {
        Application result = applicationService.createApplication(application);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result);
        response.put("message", "投保申请创建成功");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建并提交投保申请（自动进入核保流程）
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> createAndSubmit(@RequestBody Application application) {
        Application result = applicationService.createAndSubmitApplication(application);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result);
        response.put("message", "投保申请已提交，正在核保中");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getPage(@RequestBody Map<String, Object> params) {
        int pageNum = params.containsKey("pageNum") ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String status = params.containsKey("status") ? params.get("status").toString() : null;
        String applicationNo = params.containsKey("applicationNo") ? params.get("applicationNo").toString() : null;
        String productName = params.containsKey("productName") ? params.get("productName").toString() : null;
        
        IPage<Application> page = applicationService.getApplicationPage(pageNum, pageSize, status, applicationNo, productName);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        Map<String, Object> response = new HashMap<>();
        if (application != null) {
            response.put("code", 200);
            response.put("data", application);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "投保申请不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/no/{applicationNo}")
    public ResponseEntity<Map<String, Object>> getByNo(@PathVariable String applicationNo) {
        Application application = applicationService.getApplicationByNo(applicationNo);
        Map<String, Object> response = new HashMap<>();
        if (application != null) {
            response.put("code", 200);
            response.put("data", application);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "投保申请不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 提交投保申请（进入核保流程）
     */
    @PutMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submit(@PathVariable Long id) {
        Application application = applicationService.submitToUnderwriting(id);
        Map<String, Object> response = new HashMap<>();
        if (application != null) {
            response.put("code", 200);
            response.put("data", application);
            response.put("message", "提交成功，已进入核保流程");
        } else {
            response.put("code", 404);
            response.put("message", "投保申请不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        Application application = applicationService.updateApplicationStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", application);
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除投保申请（仅草稿状态可删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = applicationService.deleteApplication(id);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 400);
            response.put("message", "删除失败，只有草稿状态的投保申请可以删除");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 强制删除投保申请（忽略状态，用于测试数据清理）
     */
    @DeleteMapping("/{id}/force")
    public ResponseEntity<Map<String, Object>> forceDelete(@PathVariable Long id) {
        boolean success = applicationService.forceDeleteApplication(id);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "强制删除成功");
        } else {
            response.put("code", 404);
            response.put("message", "删除失败，投保申请不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getByApplicant(@PathVariable Long applicantId) {
        var applications = applicationService.getApplicationsByApplicant(applicantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", applications);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getByProductId(@PathVariable Long productId) {
        var applications = applicationService.getApplicationsByProductId(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", applications);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getByCustomerId(@PathVariable Long customerId) {
        var applications = applicationService.getApplicationsByCustomerId(customerId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", applications);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
}
