package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "claims-service", path = "/api/claims")
public interface ClaimsClient {

    @GetMapping("/policy/{policyId}")
    Map<String, Object> getByPolicyId(@PathVariable("policyId") Long policyId);
}
