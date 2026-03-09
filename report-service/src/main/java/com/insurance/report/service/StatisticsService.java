package com.insurance.report.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {
    
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("totalCustomers", 1250);
        data.put("totalPolicies", 3680);
        data.put("totalPremium", 15680000.00);
        data.put("totalClaims", 456);
        data.put("pendingClaims", 23);
        data.put("monthlyNewCustomers", 45);
        data.put("monthlyNewPolicies", 128);
        
        return data;
    }
    
    public Map<String, Object> getPolicyStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("totalPolicies", 3680);
        data.put("activePolicies", 3200);
        data.put("expiredPolicies", 350);
        data.put("cancelledPolicies", 130);
        data.put("totalPremium", 15680000.00);
        
        return data;
    }
    
    public Map<String, Object> getClaimStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("totalClaims", 456);
        data.put("pendingClaims", 23);
        data.put("approvedClaims", 410);
        data.put("rejectedClaims", 23);
        data.put("totalClaimAmount", 2890000.00);
        
        return data;
    }
    
    public Map<String, Object> getProductStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        Map<String, Integer> productCounts = new HashMap<>();
        productCounts.put("终身寿险", 850);
        productCounts.put("医疗保险", 1200);
        productCounts.put("意外险", 980);
        productCounts.put("年金险", 650);
        
        data.put("productCounts", productCounts);
        
        return data;
    }
}
