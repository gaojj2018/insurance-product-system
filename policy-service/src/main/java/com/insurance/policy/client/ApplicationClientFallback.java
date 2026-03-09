package com.insurance.policy.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 投保服务 Feign 降级处理
 */
@Component
public class ApplicationClientFallback implements ApplicationClient {

    @Override
    public Map<String, Object> getApplicationById(Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "投保服务暂时不可用，请稍后重试");
        result.put("data", null);
        return result;
    }
}
