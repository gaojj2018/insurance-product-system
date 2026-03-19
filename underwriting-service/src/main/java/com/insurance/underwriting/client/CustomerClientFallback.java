package com.insurance.underwriting.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomerClientFallback implements CustomerClient {
    
    @Override
    public Map<String, Object> getCustomerById(Long customerId) {
        log.warn("Customer服务不可用，无法获取客户信息: customerId={}", customerId);
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("code", 500);
        fallback.put("message", "服务不可用");
        return fallback;
    }
}
