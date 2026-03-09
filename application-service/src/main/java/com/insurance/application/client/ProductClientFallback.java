package com.insurance.application.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 产品服务 Feign 降级处理
 */
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public Map<String, Object> getProductById(Long productId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "产品服务暂时不可用，请稍后重试");
        result.put("data", null);
        return result;
    }
}
