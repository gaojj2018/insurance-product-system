package com.insurance.finance.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insurance.finance.entity.CommissionRecord;
import com.insurance.finance.entity.PremiumRecord;
import com.insurance.finance.repository.CommissionRecordMapper;
import com.insurance.finance.repository.PremiumRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionCalculateScheduler {

    private final PremiumRecordMapper premiumRecordMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.15");
    private static final BigDecimal SMALL_CLAIM_THRESHOLD = new BigDecimal("10000");

    @Scheduled(cron = "0 0 1 * * ?")
    public void calculateCommission() {
        log.info("开始计算佣金...");
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        
        LambdaQueryWrapper<CommissionRecord> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.ge(CommissionRecord::getCommissionDate, todayStart);
        existingWrapper.lt(CommissionRecord::getCommissionDate, todayEnd);
        List<CommissionRecord> existingRecords = commissionRecordMapper.selectList(existingWrapper);
        Set<String> calculatedPolicyNos = new HashSet<>();
        for (CommissionRecord record : existingRecords) {
            if (record.getPolicyNo() != null) {
                calculatedPolicyNos.add(record.getPolicyNo());
            }
        }
        log.info("今日已计算佣金数: {}", calculatedPolicyNos.size());
        
        LambdaQueryWrapper<PremiumRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PremiumRecord::getPaymentStatus, "PAID");
        wrapper.ge(PremiumRecord::getPaymentDate, todayStart);
        wrapper.lt(PremiumRecord::getPaymentDate, todayEnd);
        List<PremiumRecord> paidPremiums = premiumRecordMapper.selectList(wrapper);
        
        int commissionCount = 0;
        for (PremiumRecord premium : paidPremiums) {
            if (calculatedPolicyNos.contains(premium.getPolicyNo())) {
                log.debug("保单 {} 今日已计算佣金，跳过", premium.getPolicyNo());
                continue;
            }
            
            try {
                BigDecimal commission = calculateCommissionAmount(premium.getPremium());
                
                CommissionRecord record = new CommissionRecord();
                record.setPolicyNo(premium.getPolicyNo());
                record.setApplicationNo(premium.getApplicationNo());
                record.setProductCode(premium.getProductCode());
                record.setProductName(premium.getProductName());
                record.setPremium(premium.getPremium());
                record.setCommissionRate(COMMISSION_RATE);
                record.setCommissionAmount(commission);
                record.setCommissionStatus("PENDING");
                record.setCommissionDate(LocalDateTime.now());
                
                commissionRecordMapper.insert(record);
                commissionCount++;
                log.info("保单 {} 佣金计算完成: {}", premium.getPolicyNo(), commission);
            } catch (Exception e) {
                log.error("保单 {} 佣金计算失败: {}", premium.getPolicyNo(), e.getMessage());
            }
        }
        
        log.info("佣金计算任务完成，共计算 {} 笔", commissionCount);
    }
    
    private BigDecimal calculateCommissionAmount(BigDecimal premium) {
        if (premium == null) return BigDecimal.ZERO;
        return premium.multiply(COMMISSION_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
