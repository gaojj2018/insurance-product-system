package com.insurance.underwriting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.underwriting.client.ApplicationClient;
import com.insurance.underwriting.client.CustomerClient;
import com.insurance.underwriting.client.PolicyClient;
import com.insurance.underwriting.entity.Underwriting;
import com.insurance.underwriting.repository.UnderwritingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 核保Service - 提供核保业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnderwritingService {
    
    private final UnderwritingRepository underwritingRepository;
    private final PolicyClient policyClient;
    private final ApplicationClient applicationClient;
    private final CustomerClient customerClient;
    
    @Transactional
    public Underwriting createUnderwriting(Long applicationId, String applicationNo) {
        Underwriting uw = new Underwriting();
        uw.setApplicationId(applicationId);
        uw.setApplicationNo(applicationNo);
        uw.setType("AUTO");
        uw.setResult("PENDING");
        uw.setLevel("A");
        uw.setRiskScore(10);
        uw.setOpinion("待核保");
        uw.setEffectiveTime(LocalDateTime.now());
        uw.setExpirationTime(LocalDateTime.now().plusDays(90));
        underwritingRepository.insert(uw);
        log.info("创建核保记录成功: applicationId={}, applicationNo={}, underwritingId={}", 
                applicationId, applicationNo, uw.getUnderwritingId());
        return uw;
    }
    
    public Underwriting getByApplicationId(Long applicationId) {
        LambdaQueryWrapper<Underwriting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Underwriting::getApplicationId, applicationId);
        wrapper.orderByDesc(Underwriting::getCreatedTime);
        wrapper.last("LIMIT 1");
        return underwritingRepository.selectOne(wrapper);
    }
    
    public Underwriting getById(Long id) {
        return underwritingRepository.selectById(id);
    }
    
    public IPage<Underwriting> getPage(int pageNum, int pageSize, String result) {
        Page<Underwriting> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Underwriting> wrapper = new LambdaQueryWrapper<>();
        if (result != null && !result.isEmpty()) {
            wrapper.eq(Underwriting::getResult, result);
        }
        wrapper.orderByDesc(Underwriting::getCreatedTime);
        IPage<Underwriting> resultPage = underwritingRepository.selectPage(page, wrapper);
        
        // 补充产品名称、投保人信息、保额、保费、缴费方式、保障期间和申请时间
        List<Underwriting> records = resultPage.getRecords();
        if (records != null && !records.isEmpty()) {
            for (Underwriting uw : records) {
                try {
                    Map<String, Object> app = applicationClient.getApplication(uw.getApplicationId());
                    if (app != null && app.get("data") != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> appData = (Map<String, Object>) app.get("data");
                        uw.setProductName((String) appData.get("productName"));
                        
                        Object coverageObj = appData.get("coverage");
                        if (coverageObj != null) {
                            if (coverageObj instanceof Number) {
                                uw.setCoverageAmount(new java.math.BigDecimal(coverageObj.toString()));
                            } else if (coverageObj instanceof String) {
                                uw.setCoverageAmount(new java.math.BigDecimal((String) coverageObj));
                            }
                        }
                        Object premiumObj = appData.get("premium");
                        if (premiumObj != null) {
                            if (premiumObj instanceof Number) {
                                uw.setPremium(new java.math.BigDecimal(premiumObj.toString()));
                            } else if (premiumObj instanceof String) {
                                uw.setPremium(new java.math.BigDecimal((String) premiumObj));
                            }
                        }
                        uw.setPaymentMethod((String) appData.get("paymentMethod"));
                        uw.setCoveragePeriod((String) appData.get("coveragePeriod"));
                        
                        String applicantName = (String) appData.get("applicantName");
                        if (applicantName == null || applicantName.isEmpty()) {
                            Object applicantIdObj = appData.get("applicantId");
                            if (applicantIdObj != null) {
                                Long applicantId = applicantIdObj instanceof Number ? ((Number) applicantIdObj).longValue() : Long.parseLong(applicantIdObj.toString());
                                try {
                                    Map<String, Object> customer = customerClient.getCustomerById(applicantId);
                                    if (customer != null && customer.get("data") != null) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> customerData = (Map<String, Object>) customer.get("data");
                                        applicantName = (String) customerData.get("customerName");
                                    }
                                } catch (Exception ce) {
                                    log.warn("获取客户信息失败: applicantId={}, error={}", applicantId, ce.getMessage());
                                }
                            }
                        }
                        uw.setApplicantName(applicantName);
                        
                        if (appData.get("createdTime") != null) {
                            Object createdTimeObj = appData.get("createdTime");
                            if (createdTimeObj instanceof String) {
                                String timeStr = (String) createdTimeObj;
                                if (timeStr.contains(" ")) {
                                    timeStr = timeStr.replace(" ", "T");
                                }
                                uw.setCreatedTime(LocalDateTime.parse(timeStr));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("获取申请信息失败: applicationId={}, error={}", uw.getApplicationId(), e.getMessage());
                }
            }
        }
        
        return resultPage;
    }
    
    public List<Underwriting> getByApplicationNo(String applicationNo) {
        LambdaQueryWrapper<Underwriting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Underwriting::getApplicationNo, applicationNo);
        wrapper.orderByDesc(Underwriting::getCreatedTime);
        return underwritingRepository.selectList(wrapper);
    }
    
    /**
     * 核保通过
     */
    @Transactional
    public Underwriting approve(Long underwritingId, String level, String opinion, String underwriter) {
        Underwriting uw = underwritingRepository.selectById(underwritingId);
        if (uw == null) {
            log.warn("核保记录不存在: underwritingId={}", underwritingId);
            return null;
        }
        
        uw.setType("MANUAL");
        uw.setResult("PASS");
        uw.setLevel(level);
        uw.setOpinion(opinion);
        uw.setUnderwriter(underwriter);
        uw.setUnderwritingTime(LocalDateTime.now());
        underwritingRepository.updateById(uw);
        log.info("核保通过: underwritingId={}, applicationId={}", underwritingId, uw.getApplicationId());
        
        // 更新投保申请状态为核保通过
        try {
            applicationClient.updateStatus(uw.getApplicationId(), "APPROVED");
        } catch (Exception e) {
            log.error("更新投保申请状态失败: applicationId={}, error={}", uw.getApplicationId(), e.getMessage());
        }
        
        // 自动创建保单
        createPolicyFromUnderwriting(uw);
        
        return uw;
    }
    
    /**
     * 核保拒绝
     */
    @Transactional
    public Underwriting reject(Long underwritingId, String opinion, String underwriter) {
        Underwriting uw = underwritingRepository.selectById(underwritingId);
        if (uw == null) {
            log.warn("核保记录不存在: underwritingId={}", underwritingId);
            return null;
        }
        
        uw.setType("MANUAL");
        uw.setResult("REJECT");
        uw.setOpinion(opinion);
        uw.setUnderwriter(underwriter);
        uw.setUnderwritingTime(LocalDateTime.now());
        underwritingRepository.updateById(uw);
        log.info("核保拒绝: underwritingId={}, applicationId={}", underwritingId, uw.getApplicationId());
        
        // 更新投保申请状态为核保拒绝
        try {
            applicationClient.updateStatus(uw.getApplicationId(), "REJECTED");
        } catch (Exception e) {
            log.error("更新投保申请状态失败: applicationId={}, error={}", uw.getApplicationId(), e.getMessage());
        }
        
        return uw;
    }
    
    /**
     * 人工核保（兼容旧接口）
     */
    @Transactional
    public Underwriting manualUnderwriting(Long underwritingId, String result, String level, String opinion, String underwriter) {
        if ("PASS".equals(result)) {
            return approve(underwritingId, level, opinion, underwriter);
        } else {
            return reject(underwritingId, opinion, underwriter);
        }
    }
    
    private void createPolicyFromUnderwriting(Underwriting uw) {
        try {
            Map<String, Object> response = policyClient.createPolicyFromApplication(uw.getApplicationId());
            if (response != null && response.get("code") != null && (Integer) response.get("code") == 200) {
                log.info("核保通过，自动创建保单成功: applicationNo={}, applicationId={}", 
                        uw.getApplicationNo(), uw.getApplicationId());
                
                // 更新投保申请状态为已出单
                try {
                    applicationClient.updateStatus(uw.getApplicationId(), "POLICY_ISSUED");
                } catch (Exception e) {
                    log.error("更新投保申请状态为已出单失败: applicationId={}, error={}", 
                            uw.getApplicationId(), e.getMessage());
                }
            } else {
                log.error("创建保单失败: applicationNo={}, response={}", uw.getApplicationNo(), response);
            }
        } catch (Exception e) {
            log.error("自动创建保单失败: applicationNo={}, error={}", uw.getApplicationNo(), e.getMessage(), e);
        }
    }
    
    public List<Map<String, Object>> checkBusinessReferences(Long underwritingId) {
        List<Map<String, Object>> references = new ArrayList<>();
        
        Underwriting uw = underwritingRepository.selectById(underwritingId);
        if (uw == null) {
            return references;
        }
        
        try {
            Map<String, Object> appResponse = applicationClient.getApplication(uw.getApplicationId());
            if (appResponse != null && appResponse.get("code") != null 
                    && (Integer) appResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> app = (Map<String, Object>) appResponse.get("data");
                if (app != null && "POLICY_ISSUED".equals(app.get("status"))) {
                    references.add(Map.of(
                        "type", "APPLICATION",
                        "id", app.get("applicationId"),
                        "no", app.get("applicationNo"),
                        "status", app.get("status"),
                        "message", "投保单 " + app.get("applicationNo") + " 已出单，无法删除核保记录"
                    ));
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        return references;
    }
    
    @Transactional
    public boolean removeById(Long id) {
        Underwriting uw = underwritingRepository.selectById(id);
        if (uw == null) {
            return false;
        }
        underwritingRepository.deleteById(id);
        return true;
    }
}
