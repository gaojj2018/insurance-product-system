package com.insurance.underwriting.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 保单服务 Feign 降级处理
 */
@Component
public class PolicyClientFallback implements PolicyClient {

    @Override
    public Map<String, Object> createPolicyFromApplication(Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "保单服务暂时不可用，请稍后重试");
        result.put("data", null);
        return result;
    }
}
