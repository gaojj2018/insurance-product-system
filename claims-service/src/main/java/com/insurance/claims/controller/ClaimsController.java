package com.insurance.claims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.claims.entity.Claims;
import com.insurance.claims.service.ClaimsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 理赔管理Controller - 提供理赔相关的RESTful API
 */
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimsController {
    
    private final ClaimsService claimsService;
    
    @PostMapping("/count")
    public ResponseEntity<Map<String, Object>> count() {
        long count = claimsService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of("count", count));
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam Long policyId,
            @RequestParam String policyNo,
            @RequestParam Long applicantId,
            @RequestParam String applicantName,
            @RequestParam String accidentType,
            @RequestParam String accidentDate,
            @RequestParam String accidentLocation,
            @RequestParam String accidentDesc,
            @RequestParam BigDecimal claimAmount) {
        
        // Parse date - handle both date and datetime formats
        LocalDateTime accidentDateTime;
        try {
            // Try full datetime format first: 2026-03-10T10:00:00
            accidentDateTime = LocalDateTime.parse(accidentDate);
        } catch (Exception e) {
            try {
                // Try date only format: 2026-03-10
                LocalDate date = LocalDate.parse(accidentDate);
                accidentDateTime = date.atTime(LocalTime.now());
            } catch (Exception ex) {
                accidentDateTime = LocalDateTime.now();
            }
        }
        
        Claims claims = claimsService.createClaims(
            policyId, policyNo, applicantId, applicantName,
            accidentType, accidentDateTime, accidentLocation, accidentDesc, claimAmount
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", claims);
        response.put("message", "理赔报案成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String claimNo,
            @RequestParam(required = false) String policyNo,
            @RequestParam(required = false) String claimantName,
            @RequestParam(required = false) String status) {
        IPage<Claims> page = claimsService.getPage(pageNum, pageSize, claimNo, policyNo, claimantName, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Claims claims = claimsService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (claims != null) {
            response.put("code", 200);
            response.put("data", claims);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "理赔记录不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/no/{claimsNo}")
    public ResponseEntity<Map<String, Object>> getByNo(@PathVariable String claimsNo) {
        Claims claims = claimsService.getByClaimsNo(claimsNo);
        Map<String, Object> response = new HashMap<>();
        if (claims != null) {
            response.put("code", 200);
            response.put("data", claims);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "理赔记录不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id,
            @RequestParam BigDecimal approvedAmount,
            @RequestParam String handler) {
        Claims claims = claimsService.approve(id, approvedAmount, handler, null);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", claims);
        response.put("message", "理赔审核通过");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/pay")
    public ResponseEntity<Map<String, Object>> pay(
            @PathVariable Long id,
            @RequestParam String payAccount) {
        Claims claims = claimsService.pay(id, payAccount);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", claims);
        response.put("message", "理赔款已支付");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable Long id,
            @RequestParam String handler) {
        Claims claims = claimsService.reject(id, handler, null);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", claims);
        response.put("message", "理赔已拒绝");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = claimsService.deleteClaims(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", success ? 200 : 400);
        response.put("message", success ? "删除成功" : "删除失败");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<Map<String, Object>> getByPolicyId(@PathVariable Long policyId) {
        var claims = claimsService.getByPolicyId(policyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", claims);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = claimsService.getStatistics();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }
}
