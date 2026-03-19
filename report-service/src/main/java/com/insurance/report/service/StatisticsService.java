package com.insurance.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Object customers = restTemplate.postForObject("http://customer-service/api/customer/count", null, Object.class);
            data.put("totalCustomers", getValueFromResponse(customers, "count"));
            
            try {
                Object policyStats = restTemplate.getForObject("http://policy-service/api/policy/statistics", Object.class);
                if (policyStats instanceof Map) {
                    Map<String, Object> policyMap = (Map<String, Object>) policyStats;
                    Object statsData = policyMap.get("data");
                    if (statsData instanceof Map) {
                        Map<String, Object> statsMap = (Map<String, Object>) statsData;
                        data.put("totalPolicies", statsMap.getOrDefault("total", 0L));
                        data.put("activePolicies", statsMap.getOrDefault("active", 0L));
                        data.put("expiredPolicies", statsMap.getOrDefault("expired", 0L));
                        data.put("cancelledPolicies", statsMap.getOrDefault("cancelled", 0L));
                        data.put("totalPremium", statsMap.getOrDefault("totalPremium", BigDecimal.ZERO));
                    }
                }
            } catch (Exception e) {
                data.put("totalPolicies", 0L);
                data.put("activePolicies", 0L);
                data.put("expiredPolicies", 0L);
                data.put("cancelledPolicies", 0L);
                data.put("totalPremium", BigDecimal.ZERO);
            }
            
            try {
                Object financeStats = restTemplate.getForObject("http://finance-service/api/premium/statistics", Object.class);
                if (financeStats instanceof Map) {
                    Map<String, Object> financeMap = (Map<String, Object>) financeStats;
                    Object statsData = financeMap.get("data");
                    if (statsData instanceof Map) {
                        Map<String, Object> statsMap = (Map<String, Object>) statsData;
                        data.put("totalPremium", statsMap.getOrDefault("totalPremium", BigDecimal.ZERO));
                        data.put("monthPremium", statsMap.getOrDefault("monthPremium", BigDecimal.ZERO));
                    }
                }
            } catch (Exception e) {
            }
            
            try {
                Object claimsStats = restTemplate.getForObject("http://claims-service/api/claims/statistics", Object.class);
                if (claimsStats instanceof Map) {
                    Map<String, Object> claimsMap = (Map<String, Object>) claimsStats;
                    Object statsData = claimsMap.get("data");
                    if (statsData instanceof Map) {
                        Map<String, Object> statsMap = (Map<String, Object>) statsData;
                        data.put("pendingClaims", statsMap.getOrDefault("pendingClaims", 0));
                    }
                }
            } catch (Exception e) {
                data.put("pendingClaims", 0);
            }
            
            data.put("monthlyNewCustomers", 0);
            data.put("monthlyNewPolicies", 0);
        } catch (Exception e) {
            data.put("totalCustomers", 0);
            data.put("totalPolicies", 0);
            data.put("activePolicies", 0L);
            data.put("expiredPolicies", 0L);
            data.put("cancelledPolicies", 0L);
            data.put("totalPremium", BigDecimal.ZERO);
            data.put("monthPremium", BigDecimal.ZERO);
            data.put("pendingClaims", 0);
            data.put("monthlyNewCustomers", 0);
            data.put("monthlyNewPolicies", 0);
        }
        
        return data;
    }
    
    public Map<String, Object> getPolicyStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Object result = restTemplate.getForObject("http://policy-service/api/policy/statistics", Object.class);
            if (result instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) result;
                Object statsData = resultMap.get("data");
                if (statsData instanceof Map) {
                    Map<String, Object> statsMap = (Map<String, Object>) statsData;
                    data.put("totalPolicies", statsMap.getOrDefault("total", 0L));
                    data.put("activePolicies", statsMap.getOrDefault("active", 0L));
                    data.put("expiredPolicies", statsMap.getOrDefault("expired", 0L));
                    data.put("cancelledPolicies", statsMap.getOrDefault("cancelled", 0L));
                    data.put("totalPremium", statsMap.getOrDefault("totalPremium", BigDecimal.ZERO));
                }
            }
        } catch (Exception e) {
            data.put("totalPolicies", 0L);
            data.put("activePolicies", 0L);
            data.put("expiredPolicies", 0L);
            data.put("cancelledPolicies", 0L);
            data.put("totalPremium", BigDecimal.ZERO);
        }
        
        return data;
    }
    
    public Map<String, Object> getClaimStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Object result = restTemplate.getForObject("http://claims-service/api/claims/statistics", Object.class);
            if (result instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) result;
                Object statsData = resultMap.get("data");
                if (statsData instanceof Map) {
                    Map<String, Object> statsMap = (Map<String, Object>) statsData;
                    data.put("totalClaims", statsMap.getOrDefault("totalClaims", 0));
                    data.put("pendingClaims", statsMap.getOrDefault("pendingClaims", 0));
                    data.put("approvedClaims", statsMap.getOrDefault("approvedClaims", 0));
                    data.put("rejectedClaims", statsMap.getOrDefault("rejectedClaims", 0));
                    data.put("totalClaimAmount", statsMap.getOrDefault("totalClaimAmount", BigDecimal.ZERO));
                }
            }
        } catch (Exception e) {
            data.put("totalClaims", 0);
            data.put("pendingClaims", 0);
            data.put("approvedClaims", 0);
            data.put("rejectedClaims", 0);
            data.put("totalClaimAmount", BigDecimal.ZERO);
        }
        
        return data;
    }
    
    public Map<String, Object> getProductStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Object result = restTemplate.postForObject("http://product-service/api/product/count", null, Object.class);
            long total = getValueFromResponse(result, "count");
            
            Object typeResult = restTemplate.postForObject("http://product-service/api/product/page", 
                java.util.Map.of("pageNum", 1, "pageSize", 1000), Object.class);
            
            Map<String, Integer> productCounts = new HashMap<>();
            productCounts.put("产品总数", (int) total);
            
            if (typeResult instanceof Map) {
                Map<String, Object> typeMap = (Map<String, Object>) typeResult;
                Object typeData = typeMap.get("data");
                if (typeData instanceof Map) {
                    Object records = ((Map<String, Object>) typeData).get("records");
                    if (records instanceof java.util.List) {
                        for (Object item : (java.util.List<?>) records) {
                            if (item instanceof Map) {
                                Map<String, Object> product = (Map<String, Object>) item;
                                String type = (String) product.get("productType");
                                String typeName = getTypeName(type);
                                productCounts.put(typeName, productCounts.getOrDefault(typeName, 0) + 1);
                            }
                        }
                    }
                }
            }
            
            productCounts.remove("产品总数");
            data.put("productCounts", productCounts);
        } catch (Exception e) {
            Map<String, Integer> productCounts = new HashMap<>();
            productCounts.put("产品总数", 0);
            data.put("productCounts", productCounts);
        }
        
        return data;
    }
    
    private String getTypeName(String type) {
        if (type == null) return "其他";
        return switch (type) {
            case "LIFE" -> "人寿保险";
            case "PROPERTY" -> "财产保险";
            case "ACCIDENT" -> "意外保险";
            case "HEALTH" -> "健康保险";
            default -> type;
        };
    }
    
    private long getValueFromResponse(Object response, String field) {
        if (response instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) response;
            Object code = map.get("code");
            if (code != null && (code instanceof Integer) && (Integer) code == 200) {
                Object data = map.get("data");
                if (data instanceof Map) {
                    Object count = ((Map<String, Object>) data).get(field);
                    if (count instanceof Number) {
                        return ((Number) count).longValue();
                    }
                }
            }
        }
        return 0;
    }
}
