package com.insurance.customer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "policy-service", path = "/api/policy")
public interface PolicyClient {

    @GetMapping("/customer/{customerId}")
    Map<String, Object> getByCustomerId(@PathVariable("customerId") Long customerId);
}
