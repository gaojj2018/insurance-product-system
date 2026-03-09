package com.insurance.finance.controller;

import com.insurance.finance.entity.CommissionRecord;
import com.insurance.finance.service.CommissionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commission")
public class CommissionRecordController {
    
    @Autowired
    private CommissionRecordService commissionRecordService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CommissionRecord record) {
        CommissionRecord created = commissionRecordService.createCommissionRecord(record);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        CommissionRecord record = commissionRecordService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", record != null);
        result.put("data", record);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/policy/{policyNo}")
    public ResponseEntity<Map<String, Object>> findByPolicyNo(@PathVariable String policyNo) {
        List<CommissionRecord> records = commissionRecordService.findByPolicyNo(policyNo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Map<String, Object>> findByAgentId(@PathVariable String agentId) {
        List<CommissionRecord> records = commissionRecordService.findByAgentId(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> findPendingCommissions() {
        List<CommissionRecord> records = commissionRecordService.findPendingCommissions();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCommissionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        boolean success = commissionRecordService.updateCommissionStatus(id, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
