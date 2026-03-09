package com.insurance.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 产品服务 Feign 客户端
 */
@FeignClient(name = "insurance-product-service", path = "/api/product", fallback = ProductClientFallback.class)
public interface ProductClient {

    /**
     * 根据ID获取产品详情
     */
    @GetMapping("/{productId}")
    Map<String, Object> getProductById(@PathVariable("productId") Long productId);
}
