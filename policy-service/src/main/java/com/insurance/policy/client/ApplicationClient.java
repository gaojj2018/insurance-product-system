package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 投保服务 Feign 客户端
 */
@FeignClient(name = "application-service", path = "/api/application", fallback = ApplicationClientFallback.class)
public interface ApplicationClient {

    /**
     * 根据ID获取投保申请详情
     */
    @GetMapping("/{applicationId}")
    Map<String, Object> getApplicationById(@PathVariable("applicationId") Long applicationId);
}
