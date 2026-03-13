package com.insurance.claims.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "finance-service", path = "/api/accounting")
public interface FinanceClient {
    
    @PostMapping
    Map<String, Object> createAccountingRecord(@RequestBody Map<String, Object> record);
}
