package com.insurance.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.insurance.finance.entity.ClaimPayment;
import com.insurance.finance.entity.PremiumRecord;
import com.insurance.finance.service.ClaimPaymentService;
import com.insurance.finance.service.PremiumRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 财务统计Controller - 提供财务数据统计和分析的RESTful API
 */
@RestController
@RequestMapping("/api/statistics")
public class FinanceStatsController {

    @Autowired
    private PremiumRecordService premiumRecordService;

    @Autowired
    private ClaimPaymentService claimPaymentService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> data = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
        
        QueryWrapper<PremiumRecord> premiumWrapper = new QueryWrapper<>();
        premiumWrapper.ge("payment_date", todayStart);
        premiumWrapper.le("payment_date", todayEnd);
        premiumWrapper.eq("payment_status", "PAID");
        List<PremiumRecord> todayPremiums = premiumRecordService.list(premiumWrapper);
        BigDecimal todayPremium = todayPremiums.stream()
                .map(PremiumRecord::getPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        premiumWrapper = new QueryWrapper<>();
        premiumWrapper.ge("payment_date", monthStart);
        premiumWrapper.eq("payment_status", "PAID");
        List<PremiumRecord> monthPremiums = premiumRecordService.list(premiumWrapper);
        BigDecimal monthPremium = monthPremiums.stream()
                .map(PremiumRecord::getPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        QueryWrapper<ClaimPayment> claimWrapper = new QueryWrapper<>();
        claimWrapper.ge("create_time", monthStart);
        List<ClaimPayment> monthClaims = claimPaymentService.list(claimWrapper);
        BigDecimal monthClaim = monthClaims.stream()
                .map(ClaimPayment::getClaimAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthBalance = monthPremium.subtract(monthClaim);
        
        data.put("todayPremium", todayPremium);
        data.put("monthPremium", monthPremium);
        data.put("monthClaim", monthClaim);
        data.put("monthBalance", monthBalance);
        
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 7; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDateTime dayStart = day.atStartOfDay();
            LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();
            
            QueryWrapper<PremiumRecord> dayPremiumWrapper = new QueryWrapper<>();
            dayPremiumWrapper.ge("payment_date", dayStart);
            dayPremiumWrapper.lt("payment_date", dayEnd);
            dayPremiumWrapper.eq("payment_status", "PAID");
            List<PremiumRecord> dayPremiums = premiumRecordService.list(dayPremiumWrapper);
            BigDecimal dayPremiumTotal = dayPremiums.stream()
                    .map(PremiumRecord::getPremium)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            QueryWrapper<ClaimPayment> dayClaimWrapper = new QueryWrapper<>();
            dayClaimWrapper.ge("create_time", dayStart);
            dayClaimWrapper.lt("create_time", dayEnd);
            List<ClaimPayment> dayClaims = claimPaymentService.list(dayClaimWrapper);
            BigDecimal dayClaimTotal = dayClaims.stream()
                    .map(ClaimPayment::getClaimAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", day.format(formatter));
            dayData.put("premium", dayPremiumTotal);
            dayData.put("claim", dayClaimTotal);
            dayData.put("balance", dayPremiumTotal.subtract(dayClaimTotal));
            dayData.put("policyCount", dayPremiums.size());
            dayData.put("claimCount", dayClaims.size());
            dailyStats.add(dayData);
        }
        
        data.put("dailyStats", dailyStats);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("code", 200);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam(required = false, defaultValue = "30") int days) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        Map<String, BigDecimal> premiumByMonth = new LinkedHashMap<>();
        Map<String, BigDecimal> claimsByMonth = new LinkedHashMap<>();
        
        for (int i = 2; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            String monthLabel = monthDate.getMonthValue() + "月";
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.plusMonths(1).withDayOfMonth(1);
            
            QueryWrapper<PremiumRecord> premiumWrapper = new QueryWrapper<>();
            premiumWrapper.ge("payment_date", monthStart.atStartOfDay());
            premiumWrapper.lt("payment_date", monthEnd.atStartOfDay());
            premiumWrapper.eq("payment_status", "PAID");
            List<PremiumRecord> monthPremiums = premiumRecordService.list(premiumWrapper);
            BigDecimal monthPremium = monthPremiums.stream()
                    .map(PremiumRecord::getPremium)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            QueryWrapper<ClaimPayment> claimWrapper = new QueryWrapper<>();
            claimWrapper.ge("create_time", monthStart.atStartOfDay());
            claimWrapper.lt("create_time", monthEnd.atStartOfDay());
            List<ClaimPayment> monthClaims = claimPaymentService.list(claimWrapper);
            BigDecimal monthClaim = monthClaims.stream()
                    .map(ClaimPayment::getClaimAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            premiumByMonth.put(monthLabel, monthPremium);
            claimsByMonth.put(monthLabel, monthClaim);
        }
        
        Map<String, Object> premiumTrend = new HashMap<>();
        premiumTrend.put("labels", premiumByMonth.keySet().toArray(new String[0]));
        premiumTrend.put("values", premiumByMonth.values().toArray(new BigDecimal[0]));
        
        Map<String, Object> claimsTrend = new HashMap<>();
        claimsTrend.put("labels", claimsByMonth.keySet().toArray(new String[0]));
        claimsTrend.put("values", claimsByMonth.values().toArray(new BigDecimal[0]));
        
        result.put("premiumTrend", premiumTrend);
        result.put("claimsTrend", claimsTrend);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
