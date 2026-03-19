package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "customer-service", path = "/api/customer")
public interface CustomerClient {
    
    @GetMapping("/{id}")
    Map<String, Object> getCustomerById(@PathVariable("id") Long id);
    
    @GetMapping("/no/{customerNo}")
    Map<String, Object> getCustomerByNo(@PathVariable("customerNo") String customerNo);
}
