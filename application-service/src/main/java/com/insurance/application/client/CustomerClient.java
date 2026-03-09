package com.insurance.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "customer-service", path = "/api/customer", fallback = CustomerClientFallback.class)
public interface CustomerClient {

    @GetMapping("/{customerId}")
    Map<String, Object> getCustomerById(@PathVariable("customerId") Long customerId);
}
