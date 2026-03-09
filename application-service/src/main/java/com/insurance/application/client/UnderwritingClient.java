package com.insurance.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 核保服务 Feign 客户端
 */
@FeignClient(name = "underwriting-service", path = "/api/underwriting", fallback = UnderwritingClientFallback.class)
public interface UnderwritingClient {

    /**
     * 创建核保记录
     */
    @PostMapping
    Map<String, Object> createUnderwriting(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("applicationNo") String applicationNo);

    /**
     * 根据投保申请ID获取核保记录
     */
    @GetMapping("/application/{applicationId}")
    Map<String, Object> getByApplicationId(@PathVariable("applicationId") Long applicationId);
}
