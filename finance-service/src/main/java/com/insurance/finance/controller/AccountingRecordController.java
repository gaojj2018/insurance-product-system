package com.insurance.finance.controller;

import com.insurance.finance.entity.AccountingRecord;
import com.insurance.finance.service.AccountingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounting")
public class AccountingRecordController {
    
    @Autowired
    private AccountingRecordService accountingRecordService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody AccountingRecord record) {
        AccountingRecord created = accountingRecordService.createAccountingRecord(record);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", created);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        AccountingRecord record = accountingRecordService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", record != null);
        result.put("data", record);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/policy/{policyNo}")
    public ResponseEntity<Map<String, Object>> findByPolicyNo(@PathVariable String policyNo) {
        List<AccountingRecord> records = accountingRecordService.findByPolicyNo(policyNo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<Map<String, Object>> findByTransactionType(@PathVariable String transactionType) {
        List<AccountingRecord> records = accountingRecordService.findByTransactionType(transactionType);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/account/{accountType}")
    public ResponseEntity<Map<String, Object>> findByAccountType(@PathVariable String accountType) {
        List<AccountingRecord> records = accountingRecordService.findByAccountType(accountType);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
}
