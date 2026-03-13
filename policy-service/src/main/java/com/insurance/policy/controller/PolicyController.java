package com.insurance.policy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.policy.client.ApplicationClient;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyController {
    
    private final PolicyService policyService;
    private final ApplicationClient applicationClient;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam Long applicationId,
            @RequestParam String applicationNo,
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam String productCode,
            @RequestParam Long applicantId,
            @RequestParam Long insuredId,
            @RequestParam BigDecimal coverage,
            @RequestParam BigDecimal premium,
            @RequestParam String coveragePeriod,
            @RequestParam String paymentPeriod,
            @RequestParam String paymentMethod) {
        
        Policy policy = policyService.createPolicy(
            applicationId, applicationNo, productId, productName, productCode,
            applicantId, insuredId, coverage, premium, coveragePeriod, paymentPeriod, paymentMethod
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", policy);
        response.put("message", "保单创建成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        IPage<Policy> page = policyService.getPage(pageNum, pageSize, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Policy policy = policyService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (policy != null) {
            response.put("code", 200);
            response.put("data", policy);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "保单不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/no/{policyNo}")
    public ResponseEntity<Map<String, Object>> getByNo(@PathVariable String policyNo) {
        Policy policy = policyService.getByPolicyNo(policyNo);
        Map<String, Object> response = new HashMap<>();
        if (policy != null) {
            response.put("code", 200);
            response.put("data", policy);
            response.put("message", "查询成功");
        } else {
            response.put("code", 404);
            response.put("message", "保单不存在");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getByApplicant(@PathVariable Long applicantId) {
        var list = policyService.getByApplicant(applicantId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getByProductId(@PathVariable Long productId) {
        var list = policyService.getByProductId(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getByCustomerId(@PathVariable Long customerId) {
        var list = policyService.getByCustomerId(customerId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        response.put("message", "查询成功");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Policy policy = policyService.updateStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", policy);
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/from-application/{applicationId}")
    public ResponseEntity<Map<String, Object>> createFromApplication(@PathVariable Long applicationId) {
        try {
            Map<String, Object> appRes = applicationClient.getApplicationById(applicationId);
            
            if (appRes == null || (Integer) appRes.get("code") != 200) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "投保申请不存在");
                return ResponseEntity.ok(response);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> app = (Map<String, Object>) appRes.get("data");
            
            Policy policy = policyService.createPolicy(
                applicationId,
                (String) app.get("applicationNo"),
                ((Number) app.get("productId")).longValue(),
                (String) app.get("productName"),
                (String) app.get("productCode"),
                ((Number) app.get("applicantId")).longValue(),
                app.get("insuredId") != null ? ((Number) app.get("insuredId")).longValue() : ((Number) app.get("applicantId")).longValue(),
                new BigDecimal(app.get("coverage").toString()),
                new BigDecimal(app.get("premium").toString()),
                (String) app.get("coveragePeriod"),
                (String) app.get("paymentPeriod"),
                (String) app.get("paymentMethod")
            );
            
            log.info("从投保申请创建保单成功: applicationId={}, policyNo={}", applicationId, policy.getPolicyNo());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", policy);
            response.put("message", "保单创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("从投保申请创建保单失败: applicationId={}, error={}", applicationId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        var references = policyService.checkBusinessReferences(id);
        
        Map<String, Object> result = new HashMap<>();
        if (!references.isEmpty()) {
            result.put("code", 400);
            result.put("message", "该保单有关联的业务记录，无法删除");
            result.put("data", references);
            return ResponseEntity.ok(result);
        }
        
        boolean success = policyService.removeById(id);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/surrender")
    public ResponseEntity<Map<String, Object>> surrender(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        boolean success = policyService.surrender(id, reason);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "退保成功");
        } else {
            result.put("code", 400);
            result.put("message", "退保失败，保单不存在或状态不允许退保");
        }
        return ResponseEntity.ok(result);
    }
}
