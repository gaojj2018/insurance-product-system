package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "finance-service", path = "/api/accounting")
public interface AccountingClient {

    @GetMapping("/policy/{policyNo}")
    Map<String, Object> getByPolicyNo(@PathVariable("policyNo") String policyNo);
}
