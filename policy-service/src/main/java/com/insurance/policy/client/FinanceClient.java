package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "finance-service", path = "/api/premium")
public interface FinanceClient {

    @GetMapping("/policy/{policyNo}")
    Map<String, Object> getPremiumByPolicyNo(@PathVariable("policyNo") String policyNo);
}
