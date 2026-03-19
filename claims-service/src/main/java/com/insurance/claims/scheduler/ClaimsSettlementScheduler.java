package com.insurance.claims.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insurance.claims.entity.Claims;
import com.insurance.claims.repository.ClaimsRepository;
import com.insurance.claims.service.ClaimsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimsSettlementScheduler {

    private final ClaimsRepository claimsRepository;
    private final ClaimsService claimsService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void processClaimsSettlement() {
        log.info("开始处理理赔结算任务...");
        
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Claims::getStatus, "APPROVED");
        List<Claims> approvedClaims = claimsRepository.selectList(wrapper);
        
        int settlementCount = 0;
        for (Claims claims : approvedClaims) {
            try {
                claimsService.processSettlement(claims.getClaimsId());
                settlementCount++;
                log.info("理赔单 {} 结算完成", claims.getClaimsNo());
            } catch (Exception e) {
                log.error("理赔单 {} 结算失败: {}", claims.getClaimsNo(), e.getMessage());
            }
        }
        
        log.info("理赔结算任务完成，已结算 {} 单", settlementCount);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void autoApproveSmallClaims() {
        log.info("开始处理小额理赔自动审核任务...");
        
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Claims::getStatus, "REPORTED");
        List<Claims> reportedClaims = claimsRepository.selectList(wrapper);
        
        int autoApprovedCount = 0;
        for (Claims claims : reportedClaims) {
            if (claims.getClaimAmount() != null && claims.getClaimAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
                try {
                    claims.setStatus("APPROVED");
                    claims.setApprovedAmount(claims.getClaimAmount());
                    claims.setHandleTime(LocalDateTime.now());
                    claimsRepository.updateById(claims);
                    autoApprovedCount++;
                    log.info("理赔单 {} 自动审核通过 (小额)", claims.getClaimsNo());
                } catch (Exception e) {
                    log.error("理赔单 {} 自动审核失败: {}", claims.getClaimsNo(), e.getMessage());
                }
            }
        }
        
        log.info("小额理赔自动审核完成，自动通过 {} 单", autoApprovedCount);
    }
}
