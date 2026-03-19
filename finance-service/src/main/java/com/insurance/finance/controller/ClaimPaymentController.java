package com.insurance.finance.controller;

import com.insurance.finance.entity.ClaimPayment;
import com.insurance.finance.service.ClaimPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/claim-payment")
public class ClaimPaymentController {
    
    @Autowired
    private ClaimPaymentService claimPaymentService;
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> findPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        var page = claimPaymentService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize));
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", page);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = claimPaymentService.removeById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 400);
        result.put("message", success ? "删除成功" : "删除失败");
        return ResponseEntity.ok(result);
    }
}
