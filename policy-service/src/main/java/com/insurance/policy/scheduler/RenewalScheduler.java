package com.insurance.policy.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insurance.policy.client.CustomerClient;
import com.insurance.policy.client.MessageClient;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RenewalScheduler {

    private final PolicyRepository policyRepository;
    private final MessageClient messageClient;
    private final CustomerClient customerClient;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "0 0 2 * * ?")
    public void processRenewals() {
        log.info("开始处理续期任务...");
        
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Policy::getStatus, "EFFECTIVE");
        List<Policy> activePolicies = policyRepository.selectList(wrapper);
        
        int renewalCount = 0;
        int expiredCount = 0;
        int notifiedCount = 0;
        
        for (Policy policy : activePolicies) {
            if (policy.getExpirationDate() != null) {
                LocalDate expiryDate = policy.getExpirationDate().toLocalDate();
                long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                
                if (daysUntilExpiry <= 30 && daysUntilExpiry > 0) {
                    log.info("保单 {} 将在 {} 天后到期", policy.getPolicyNo(), daysUntilExpiry);
                    renewalCount++;
                    
                    if (daysUntilExpiry == 30 || daysUntilExpiry == 15 || daysUntilExpiry == 7) {
                        sendRenewalNotification(policy, expiryDate);
                        notifiedCount++;
                    }
                }
                
                if (daysUntilExpiry <= 0) {
                    policy.setStatus("EXPIRED");
                    policy.setUpdatedTime(LocalDateTime.now());
                    policyRepository.updateById(policy);
                    log.info("保单 {} 已过期", policy.getPolicyNo());
                    expiredCount++;
                }
            }
        }
        
        log.info("续期任务完成，待续期保单数: {}, 已过期: {}, 已发送提醒: {}", renewalCount, expiredCount, notifiedCount);
    }
    
    private void sendRenewalNotification(Policy policy, LocalDate expiryDate) {
        try {
            Object customerResult = customerClient.getCustomerById(policy.getInsuredId());
            if (customerResult instanceof Map) {
                Map<String, Object> customerData = (Map<String, Object>) customerResult;
                Object data = customerData.get("data");
                if (data instanceof Map) {
                    Map<String, Object> customer = (Map<String, Object>) data;
                    String mobile = (String) customer.get("phone");
                    if (mobile != null && !mobile.isEmpty()) {
                        messageClient.sendRenewalNotification(
                                mobile,
                                policy.getPolicyNo(),
                                expiryDate.format(DATE_FORMATTER));
                        log.info("已发送续期提醒给保单 {}", policy.getPolicyNo());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("发送续期提醒失败，保单 {}: {}", policy.getPolicyNo(), e.getMessage());
        }
    }
}
