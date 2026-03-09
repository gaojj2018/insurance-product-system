package com.insurance.application.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerClientFallback implements CustomerClient {

    @Override
    public Map<String, Object> getCustomerById(Long customerId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "客户服务暂时不可用");
        result.put("data", null);
        return result;
    }
}
