package com.insurance.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class FinanceStatsController {

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> data = new HashMap<>();
        
        // 统计指标
        data.put("todayPremium", 3100.00);
        data.put("monthPremium", 6200.00);
        data.put("monthClaim", 0.00);
        data.put("monthBalance", 5580.00);
        
        // 列表数据
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        
        // 模拟每日数据
        String[] days = {"2026-03-01", "2026-03-02", "2026-03-03", "2026-03-04", "2026-03-05", 
                         "2026-03-06", "2026-03-07", "2026-03-08"};
        Double[] premiums = {1000.0, 800.0, 1200.0, 600.0, 900.0, 1100.0, 300.0, 200.0};
        Double[] claims = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", days[i]);
            day.put("premium", premiums[i]);
            day.put("claim", claims[i]);
            day.put("balance", premiums[i] - claims[i]);
            day.put("policyCount", i + 1);
            day.put("claimCount", 0);
            dailyStats.add(day);
        }
        
        data.put("dailyStats", dailyStats);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("code", 200);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam(required = false, defaultValue = "30") int days) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> premiumTrend = new HashMap<>();
        premiumTrend.put("labels", new String[]{"1月", "2月", "3月"});
        premiumTrend.put("values", new Double[]{1000.0, 2500.0, 2700.0});
        
        Map<String, Object> claimsTrend = new HashMap<>();
        claimsTrend.put("labels", new String[]{"1月", "2月", "3月"});
        claimsTrend.put("values", new Double[]{0.0, 0.0, 0.0});
        
        result.put("premiumTrend", premiumTrend);
        result.put("claimsTrend", claimsTrend);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
