package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@FeignClient(name = "finance-service", path = "/api/premium")
public interface FinanceClient {

    @GetMapping("/policy/{policyNo}")
    Map<String, Object> getPremiumByPolicyNo(@PathVariable("policyNo") String policyNo);
    
    @PostMapping
    Map<String, Object> createPremiumRecord(@org.springframework.web.bind.annotation.RequestBody Map<String, Object> record);
    
    @PutMapping("/policy/{policyNo}/refund")
    Map<String, Object> refundPremiumByPolicyNo(@PathVariable("policyNo") String policyNo);
}
