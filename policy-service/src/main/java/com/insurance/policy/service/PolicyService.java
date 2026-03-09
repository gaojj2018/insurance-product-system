package com.insurance.policy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    
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
}
