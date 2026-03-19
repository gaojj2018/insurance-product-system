package com.insurance.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.finance.entity.PremiumRecord;
import com.insurance.finance.service.PremiumRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/premium")
public class PremiumRecordController {
    
    @Autowired
    private PremiumRecordService premiumRecordService;
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(@RequestBody Map<String, Object> params) {
        int pageNum = 1;
        int pageSize = 10;
        if (params.get("pageNum") != null) {
            pageNum = Integer.parseInt(params.get("pageNum").toString());
        }
        if (params.get("pageSize") != null) {
            pageSize = Integer.parseInt(params.get("pageSize").toString());
        }
        String policyNo = params.containsKey("policyNo") && params.get("policyNo") != null ? params.get("policyNo").toString() : null;
        String customerName = params.containsKey("customerName") && params.get("customerName") != null ? params.get("customerName").toString() : null;
        String paymentStatus = params.containsKey("paymentStatus") && params.get("paymentStatus") != null ? params.get("paymentStatus").toString() : null;
        Page<PremiumRecord> page = premiumRecordService.findPage(pageNum, pageSize, policyNo, customerName, paymentStatus);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody PremiumRecord record) {
        PremiumRecord created = premiumRecordService.createPremiumRecord(record);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        PremiumRecord record = premiumRecordService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", record != null);
        result.put("data", record);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/policy/{policyNo}")
    public ResponseEntity<Map<String, Object>> findByPolicyNo(@PathVariable String policyNo) {
        List<PremiumRecord> records = premiumRecordService.findByPolicyNo(policyNo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/application/{applicationNo}")
    public ResponseEntity<Map<String, Object>> findByApplicationNo(@PathVariable String applicationNo) {
        List<PremiumRecord> records = premiumRecordService.findByApplicationNo(applicationNo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> findPendingPayments() {
        List<PremiumRecord> records = premiumRecordService.findPendingPayments();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/payment")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) LocalDateTime paymentDate) {
        boolean success = premiumRecordService.updatePaymentStatus(id, status, paymentDate);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/confirm/{id}")
    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable Long id) {
        boolean success = premiumRecordService.updatePaymentStatus(id, "PAID", LocalDateTime.now());
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "确认成功" : "确认失败");
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/policy/{policyNo}/refund")
    public ResponseEntity<Map<String, Object>> refundByPolicyNo(@PathVariable String policyNo) {
        int count = premiumRecordService.refundByPolicyNo(policyNo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退保处理成功，涉及 " + count + " 条保费记录");
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = premiumRecordService.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除失败");
        }
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAll() {
        premiumRecordService.clearAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "数据已清空");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = premiumRecordService.getStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", stats);
        return ResponseEntity.ok(result);
    }
}
