package com.insurance.application.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 核保服务 Feign 降级处理
 */
@Component
public class UnderwritingClientFallback implements UnderwritingClient {

    @Override
    public Map<String, Object> createUnderwriting(Long applicationId, String applicationNo) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "核保服务暂时不可用，请稍后重试");
        result.put("data", null);
        return result;
    }

    @Override
    public Map<String, Object> getByApplicationId(Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "核保服务暂时不可用，请稍后重试");
        result.put("data", null);
        return result;
    }
}
