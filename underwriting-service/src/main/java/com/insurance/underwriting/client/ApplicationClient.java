package com.insurance.underwriting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 投保服务 Feign 客户端
 */
@FeignClient(name = "application-service", path = "/api/application", fallback = ApplicationClientFallback.class)
public interface ApplicationClient {

    /**
     * 获取投保申请详情
     */
    @GetMapping("/{id}")
    Map<String, Object> getApplication(@PathVariable("id") Long id);
    
    /**
     * 更新投保申请状态
     */
    @PutMapping("/{id}/status")
    Map<String, Object> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status);
}
