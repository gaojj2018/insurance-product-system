package com.insurance.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "policy-service", path = "/api/policy")
public interface PolicyClient {

    @GetMapping("/product/{productId}")
    Map<String, Object> getByProductId(@PathVariable("productId") Long productId);
}
