package com.insurance.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.application.client.CustomerClient;
import com.insurance.application.client.ProductClient;
import com.insurance.application.client.UnderwritingClient;
import com.insurance.application.entity.Application;
import com.insurance.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final UnderwritingClient underwritingClient;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    @Transactional
    public Application createApplication(Application application) {
        // 生成投保单号
        String applicationNo = "APP" + LocalDateTime.now().format(FORMATTER) + String.format("%04d", RANDOM.nextInt(10000));
        application.setApplicationNo(applicationNo);
        application.setStatus("DRAFT");
        
        // 如果没有产品名称，从产品服务获取
        if (application.getProductId() != null && (application.getProductName() == null || application.getProductName().isEmpty())) {
            try {
                Map<String, Object> response = productClient.getProductById(application.getProductId());
                if (response != null && response.get("code") != null && (Integer) response.get("code") == 200) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null) {
                        application.setProductName((String) data.get("productName"));
                        application.setProductCode((String) data.get("productCode"));
                        application.setCoveragePeriod((String) data.get("coveragePeriod"));
                        application.setPaymentPeriod((String) data.get("paymentPeriod"));
                        log.info("从产品服务获取产品信息成功: productId={}, productName={}", 
                                application.getProductId(), application.getProductName());
                    }
                }
            } catch (Exception e) {
                // 产品服务不可用时使用默认值
                log.warn("获取产品信息失败，使用默认值: productId={}, error={}", 
                        application.getProductId(), e.getMessage());
                application.setProductName("产品" + application.getProductId());
                application.setProductCode("PROD-" + application.getProductId());
            }
        }
        
        // 如果有投保人ID，获取投保人姓名
        if (application.getApplicantId() != null && (application.getApplicantName() == null || application.getApplicantName().isEmpty())) {
            try {
                Map<String, Object> response = customerClient.getCustomerById(application.getApplicantId());
                if (response != null && response.get("code") != null && (Integer) response.get("code") == 200) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null) {
                        application.setApplicantName((String) data.get("name"));
                        log.info("从客户服务获取投保人信息成功: applicantId={}, applicantName={}", 
                                application.getApplicantId(), application.getApplicantName());
                    }
                }
            } catch (Exception e) {
                log.warn("获取投保人信息失败: applicantId={}, error={}", 
                        application.getApplicantId(), e.getMessage());
            }
        }
        
        // 计算保费（简化计算：保额 * 0.01）
        if (application.getCoverage() != null) {
            application.setPremium(application.getCoverage().multiply(new BigDecimal("0.01")));
        }
        
        applicationRepository.insert(application);
        log.info("创建投保申请成功: applicationNo={}", applicationNo);
        return application;
    }
    
    /**
     * 创建并提交投保申请（自动进入核保流程）
     */
    @Transactional
    public Application createAndSubmitApplication(Application application) {
        // 先创建投保申请
        Application created = createApplication(application);
        
        // 自动提交并进入核保流程
        submitToUnderwriting(created.getApplicationId());
        
        return applicationRepository.selectById(created.getApplicationId());
    }
    
    /**
     * 提交投保申请并自动创建核保记录
     */
    @Transactional
    public Application submitToUnderwriting(Long id) {
        Application application = applicationRepository.selectById(id);
        if (application == null) {
            log.warn("投保申请不存在: id={}", id);
            return null;
        }
        
        // 更新状态为核保中
        application.setStatus("UNDERWRITING");
        applicationRepository.updateById(application);
        
        // 自动创建核保记录
        try {
            Map<String, Object> response = underwritingClient.createUnderwriting(
                    application.getApplicationId(), 
                    application.getApplicationNo());
            
            if (response != null && response.get("code") != null && (Integer) response.get("code") == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("applicationId") != null) {
                    // 保存核保ID
                    Object uwId = data.get("applicationId");
                    if (uwId instanceof Number) {
                        application.setUnderwritingId(((Number) uwId).longValue());
                    }
                    applicationRepository.updateById(application);
                }
                log.info("自动创建核保记录成功: applicationNo={}, applicationId={}", 
                        application.getApplicationNo(), application.getApplicationId());
            }
        } catch (Exception e) {
            log.error("创建核保记录失败: applicationNo={}, error={}", 
                    application.getApplicationNo(), e.getMessage(), e);
            // 核保服务不可用时，状态回退
            application.setStatus("SUBMITTED");
            applicationRepository.updateById(application);
        }
        
        return application;
    }
    
    public IPage<Application> getApplicationPage(int pageNum, int pageSize, String status) {
        Page<Application> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Application::getStatus, status);
        }
        wrapper.orderByDesc(Application::getCreatedTime);
        return applicationRepository.selectPage(page, wrapper);
    }
    
    public Application getApplicationById(Long id) {
        Application app = applicationRepository.selectById(id);
        if (app != null && app.getApplicantId() != null && (app.getApplicantName() == null || app.getApplicantName().isEmpty())) {
            try {
                Map<String, Object> response = customerClient.getCustomerById(app.getApplicantId());
                if (response != null && response.get("code") != null && (Integer) response.get("code") == 200) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null) {
                        app.setApplicantName((String) data.get("name"));
                    }
                }
            } catch (Exception e) {
                log.warn("获取投保人信息失败: applicantId={}, error={}", app.getApplicantId(), e.getMessage());
            }
        }
        return app;
    }
    
    public Application getApplicationByNo(String applicationNo) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicationNo, applicationNo);
        return applicationRepository.selectOne(wrapper);
    }
    
    @Transactional
    public Application submitApplication(Long id) {
        Application application = applicationRepository.selectById(id);
        if (application != null) {
            application.setStatus("SUBMITTED");
            applicationRepository.updateById(application);
            log.info("提交投保申请成功: id={}, applicationNo={}", id, application.getApplicationNo());
        }
        return application;
    }
    
    @Transactional
    public Application updateApplicationStatus(Long id, String status) {
        Application application = applicationRepository.selectById(id);
        if (application != null) {
            application.setStatus(status);
            applicationRepository.updateById(application);
            log.info("更新投保申请状态: id={}, status={}", id, status);
        }
        return application;
    }
    
    /**
     * 更新投保申请的保单ID（核保通过后调用）
     */
    @Transactional
    public void updatePolicyId(Long applicationId, Long policyId) {
        Application application = applicationRepository.selectById(applicationId);
        if (application != null) {
            application.setPolicyId(policyId);
            application.setStatus("POLICY_ISSUED");
            applicationRepository.updateById(application);
            log.info("更新投保申请保单ID: applicationId={}, policyId={}", applicationId, policyId);
        }
    }
    
    /**
     * 删除投保申请（仅草稿状态可删除）
     */
    @Transactional
    public boolean deleteApplication(Long id) {
        Application application = applicationRepository.selectById(id);
        if (application == null) {
            log.warn("投保申请不存在: id={}", id);
            return false;
        }
        
        // 只有草稿状态可以删除
        if (!"DRAFT".equals(application.getStatus())) {
            log.warn("只有草稿状态的投保申请可以删除: id={}, status={}", id, application.getStatus());
            return false;
        }
        
        int rows = applicationRepository.deleteById(id);
        log.info("删除投保申请: id={}, applicationNo={}, rows={}", id, application.getApplicationNo(), rows);
        return rows > 0;
    }
    
    public List<Application> getApplicationsByApplicant(Long applicantId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId, applicantId);
        wrapper.orderByDesc(Application::getCreatedTime);
        return applicationRepository.selectList(wrapper);
    }
}
