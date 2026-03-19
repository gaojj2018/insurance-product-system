package com.insurance.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.finance.entity.PremiumRecord;
import com.insurance.finance.repository.PremiumRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保费记录Service - 提供保费记录业务逻辑处理
 */
    @Service
public class PremiumRecordService extends ServiceImpl<PremiumRecordMapper, PremiumRecord> {
    
    private final RestTemplate restTemplate;
    
    public PremiumRecordService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Transactional
    public PremiumRecord createPremiumRecord(PremiumRecord record) {
        record.setPaymentStatus("PENDING");
        record.setInvoiceStatus("NOT_ISSUED");
        this.save(record);
        return record;
    }
    
    public Page<PremiumRecord> findPage(int pageNum, int pageSize, String policyNo, String customerName, String paymentStatus) {
        Page<PremiumRecord> page = new Page<>(pageNum, pageSize, 0);
        LambdaQueryWrapper<PremiumRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(policyNo)) {
            wrapper.like(PremiumRecord::getPolicyNo, policyNo);
        }
        if (StringUtils.hasText(paymentStatus)) {
            wrapper.eq(PremiumRecord::getPaymentStatus, paymentStatus);
        }
        wrapper.orderByDesc(PremiumRecord::getCreateTime);
        Page<PremiumRecord> result = this.page(page, wrapper);
        
        if (StringUtils.hasText(customerName)) {
            result.setRecords(result.getRecords().stream()
                .filter(r -> {
                    if (r.getCustomerName() != null && r.getCustomerName().contains(customerName)) {
                        return true;
                    }
                    try {
                        String url = "http://policy-service/api/policy/no/" + r.getPolicyNo();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                        if (response != null && response.get("data") != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> policyData = (Map<String, Object>) response.get("data");
                            Object applicantName = policyData.get("applicantName");
                            if (applicantName != null && applicantName.toString().contains(customerName)) {
                                r.setCustomerName(applicantName.toString());
                                return true;
                            }
                        }
                    } catch (Exception e) {
                    }
                    return false;
                })
                .collect(java.util.stream.Collectors.toList()));
            result.setTotal(result.getRecords().size());
        } else {
            for (PremiumRecord record : result.getRecords()) {
                if (record.getPolicyNo() != null && StringUtils.hasText(record.getPolicyNo()) && record.getCustomerName() == null) {
                    try {
                        String url = "http://policy-service/api/policy/no/" + record.getPolicyNo();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                        if (response != null && response.get("data") != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> policyData = (Map<String, Object>) response.get("data");
                            Object applicantName = policyData.get("applicantName");
                            if (applicantName != null) {
                                record.setCustomerName(applicantName.toString());
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        
        return result;
    }
    
    public List<PremiumRecord> findByPolicyNo(String policyNo) {
        return this.list(new QueryWrapper<PremiumRecord>()
                .eq("policy_no", policyNo)
                .orderByDesc("create_time"));
    }
    
    public List<PremiumRecord> findByApplicationNo(String applicationNo) {
        return this.list(new QueryWrapper<PremiumRecord>()
                .eq("application_no", applicationNo)
                .orderByDesc("create_time"));
    }
    
    @Transactional
    public boolean updatePaymentStatus(Long id, String status, LocalDateTime paymentDate) {
        PremiumRecord record = this.getById(id);
        if (record != null) {
            record.setPaymentStatus(status);
            if (paymentDate != null) {
                record.setPaymentDate(paymentDate);
            }
            if ("PAID".equals(status) && (record.getPaymentBatchNo() == null || record.getPaymentBatchNo().isEmpty())) {
                String batchNo = "BAT" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                               + String.format("%04d", id % 10000);
                record.setPaymentBatchNo(batchNo);
            }
            return this.updateById(record);
        }
        return false;
    }
    
    @Transactional
    public int refundByPolicyNo(String policyNo) {
        List<PremiumRecord> records = this.list(new QueryWrapper<PremiumRecord>()
                .eq("policy_no", policyNo));
        int count = 0;
        for (PremiumRecord record : records) {
            if ("PAID".equals(record.getPaymentStatus())) {
                record.setPaymentStatus("REFUNDED");
                record.setPaymentDate(LocalDateTime.now());
                this.updateById(record);
                count++;
            } else if ("PENDING".equals(record.getPaymentStatus())) {
                record.setPaymentStatus("SURRENDERED");
                this.updateById(record);
                count++;
            }
        }
        return count;
    }
    
    public List<PremiumRecord> findPendingPayments() {
        return this.list(new QueryWrapper<PremiumRecord>()
                .eq("payment_status", "PENDING")
                .orderByAsc("create_time"));
    }
    
    @Transactional
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }
    
    @Transactional
    public void clearAll() {
        this.remove(null);
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();
        
        QueryWrapper<PremiumRecord> allWrapper = new QueryWrapper<>();
        allWrapper.eq("payment_status", "PAID");
        List<PremiumRecord> allPaid = this.list(allWrapper);
        BigDecimal totalPremium = allPaid.stream()
                .map(PremiumRecord::getPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        QueryWrapper<PremiumRecord> monthWrapper = new QueryWrapper<>();
        monthWrapper.eq("payment_status", "PAID");
        monthWrapper.ge("payment_date", monthStart);
        monthWrapper.lt("payment_date", monthEnd);
        List<PremiumRecord> monthPaid = this.list(monthWrapper);
        BigDecimal monthPremium = monthPaid.stream()
                .map(PremiumRecord::getPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        QueryWrapper<PremiumRecord> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("payment_status", "PENDING");
        long pendingCount = this.count(pendingWrapper);
        
        stats.put("totalPremium", totalPremium);
        stats.put("monthPremium", monthPremium);
        stats.put("pendingCount", pendingCount);
        stats.put("totalCount", this.count());
        
        return stats;
    }
}
