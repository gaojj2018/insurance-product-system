package com.insurance.underwriting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 保单服务 Feign 客户端
 */
@FeignClient(name = "policy-service", path = "/api/policy", fallback = PolicyClientFallback.class)
public interface PolicyClient {

    /**
     * 根据投保申请创建保单
     */
    @PostMapping("/from-application/{applicationId}")
    Map<String, Object> createPolicyFromApplication(@PathVariable("applicationId") Long applicationId);
}
