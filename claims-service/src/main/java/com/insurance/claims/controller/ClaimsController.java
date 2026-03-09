package com.insurance.claims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.claims.entity.Claims;
import com.insurance.claims.service.ClaimsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimsController {
    
    private final ClaimsService claimsService;
    
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
        
        Claims claims = claimsService.createClaims(
            policyId, policyNo, applicantId, applicantName,
            accidentType, LocalDateTime.parse(accidentDate), accidentLocation, accidentDesc, claimAmount
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
            @RequestParam(required = false) String status) {
        IPage<Claims> page = claimsService.getPage(pageNum, pageSize, status);
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
}
