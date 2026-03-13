package com.insurance.policy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.policy.client.AccountingClient;
import com.insurance.policy.client.ClaimsClient;
import com.insurance.policy.client.FinanceClient;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    private final ClaimsClient claimsClient;
    private final FinanceClient financeClient;
    private final AccountingClient accountingClient;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();
    
    @Transactional
    public Policy createPolicy(Long applicationId, String applicationNo, Long productId, String productName,
                                String productCode, Long applicantId, Long insuredId, 
                                java.math.BigDecimal coverage, java.math.BigDecimal premium,
                                String coveragePeriod, String paymentPeriod, String paymentMethod) {
        Policy policy = new Policy();
        policy.setApplicationId(applicationId);
        policy.setApplicationNo(applicationNo);
        policy.setProductId(productId);
        policy.setProductName(productName);
        policy.setProductCode(productCode);
        policy.setApplicantId(applicantId);
        policy.setInsuredId(insuredId);
        policy.setCoverage(coverage);
        policy.setPremium(premium);
        policy.setCoveragePeriod(coveragePeriod);
        policy.setPaymentPeriod(paymentPeriod);
        policy.setPaymentMethod(paymentMethod);
        
        String policyNo = "POL" + LocalDateTime.now().format(FORMATTER) + String.format("%06d", RANDOM.nextInt(1000000));
        policy.setPolicyNo(policyNo);
        policy.setStatus("EFFECTIVE");
        
        LocalDateTime now = LocalDateTime.now();
        policy.setIssueDate(now);
        policy.setEffectiveDate(now);
        policy.setExpirationDate(now.plusYears(1));
        policy.setNextPaymentDate(now.plusMonths(12));
        
        policyRepository.insert(policy);
        return policy;
    }
    
    public IPage<Policy> getPage(int pageNum, int pageSize, String status) {
        Page<Policy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Policy::getStatus, status);
        }
        wrapper.orderByDesc(Policy::getCreatedTime);
        return policyRepository.selectPage(page, wrapper);
    }
    
    public Policy getById(Long id) {
        return policyRepository.selectById(id);
    }
    
    public Policy getByPolicyNo(String policyNo) {
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Policy::getPolicyNo, policyNo);
        return policyRepository.selectOne(wrapper);
    }
    
    public List<Policy> getByApplicant(Long applicantId) {
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Policy::getApplicantId, applicantId);
        wrapper.orderByDesc(Policy::getCreatedTime);
        return policyRepository.selectList(wrapper);
    }
    
    @Transactional
    public Policy updateStatus(Long id, String status) {
        Policy policy = policyRepository.selectById(id);
        if (policy != null) {
            policy.setStatus(status);
            policyRepository.updateById(policy);
        }
        return policy;
    }
    
    public List<Policy> getByProductId(Long productId) {
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Policy::getProductId, productId);
        wrapper.notIn(Policy::getStatus, "TERMINATED", "EXPIRED", "SURRENDERED", "LAPSED");
        wrapper.orderByDesc(Policy::getCreatedTime);
        return policyRepository.selectList(wrapper);
    }
    
    public List<Policy> getByCustomerId(Long customerId) {
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Policy::getApplicantId, customerId)
                       .or().eq(Policy::getInsuredId, customerId));
        wrapper.notIn(Policy::getStatus, "TERMINATED", "EXPIRED", "SURRENDERED", "LAPSED");
        wrapper.orderByDesc(Policy::getCreatedTime);
        return policyRepository.selectList(wrapper);
    }
    
    public List<Map<String, Object>> checkBusinessReferences(Long policyId) {
        List<Map<String, Object>> references = new ArrayList<>();
        
        Policy policy = policyRepository.selectById(policyId);
        if (policy == null) {
            return references;
        }
        
        try {
            Map<String, Object> claimsResponse = claimsClient.getByPolicyId(policyId);
            if (claimsResponse != null && claimsResponse.get("code") != null 
                    && (Integer) claimsResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> claimsList = (List<Map<String, Object>>) claimsResponse.get("data");
                if (claimsList != null && !claimsList.isEmpty()) {
                    for (Map<String, Object> claim : claimsList) {
                        references.add(Map.of(
                            "type", "CLAIMS",
                            "id", claim.get("claimsId"),
                            "no", claim.get("claimsNo"),
                            "status", claim.get("status"),
                            "message", "理赔 " + claim.get("claimsNo") + " (状态: " + claim.get("status") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        try {
            Map<String, Object> premiumResponse = financeClient.getPremiumByPolicyNo(policy.getPolicyNo());
            if (premiumResponse != null && premiumResponse.get("success") != null 
                    && (Boolean) premiumResponse.get("success")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> premiums = (List<Map<String, Object>>) premiumResponse.get("data");
                if (premiums != null && !premiums.isEmpty()) {
                    for (Map<String, Object> premium : premiums) {
                        references.add(Map.of(
                            "type", "PREMIUM",
                            "id", premium.get("id"),
                            "no", premium.get("policyNo"),
                            "status", premium.get("paymentStatus"),
                            "message", "保费记录 " + premium.get("policyNo") + " (状态: " + premium.get("paymentStatus") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        try {
            Map<String, Object> accountingResponse = accountingClient.getByPolicyNo(policy.getPolicyNo());
            if (accountingResponse != null && accountingResponse.get("success") != null 
                    && (Boolean) accountingResponse.get("success")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> records = (List<Map<String, Object>>) accountingResponse.get("data");
                if (records != null && !records.isEmpty()) {
                    for (Map<String, Object> record : records) {
                        references.add(Map.of(
                            "type", "ACCOUNTING",
                            "id", record.get("id"),
                            "no", record.get("policyNo"),
                            "status", record.get("accountType"),
                            "message", "财务记录 " + record.get("policyNo") + " (类型: " + record.get("accountType") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        return references;
    }
    
    @Transactional
    public boolean removeById(Long id) {
        Policy policy = policyRepository.selectById(id);
        if (policy == null) {
            return false;
        }
        policyRepository.deleteById(id);
        return true;
    }
    
    @Transactional
    public boolean surrender(Long id, String reason) {
        Policy policy = policyRepository.selectById(id);
        if (policy == null) {
            return false;
        }
        // Only EFFECTIVE or ACTIVE status can surrender
        if (!"EFFECTIVE".equals(policy.getStatus()) && !"ACTIVE".equals(policy.getStatus())) {
            return false;
        }
        policy.setStatus("SURRENDERED");
        policyRepository.updateById(policy);
        return true;
    }
}
