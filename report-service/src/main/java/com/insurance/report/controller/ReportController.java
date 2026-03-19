package com.insurance.report.controller;

import com.insurance.report.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据报表Controller - 提供统计数据和报表相关的RESTful API
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> data = statisticsService.getDashboard();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/policy")
    public ResponseEntity<Map<String, Object>> getPolicyStatistics() {
        Map<String, Object> data = statisticsService.getPolicyStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/claim")
    public ResponseEntity<Map<String, Object>> getClaimStatistics() {
        Map<String, Object> data = statisticsService.getClaimStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/product")
    public ResponseEntity<Map<String, Object>> getProductStatistics() {
        Map<String, Object> data = statisticsService.getProductStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
}
