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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PremiumRecordService extends ServiceImpl<PremiumRecordMapper, PremiumRecord> {
    
    @Transactional
    public PremiumRecord createPremiumRecord(PremiumRecord record) {
        record.setPaymentStatus("PENDING");
        record.setInvoiceStatus("NOT_ISSUED");
        this.save(record);
        return record;
    }
    
    public Page<PremiumRecord> findPage(int pageNum, int pageSize, String policyNo, String customerName, String paymentStatus) {
        Page<PremiumRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PremiumRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(policyNo)) {
            wrapper.like(PremiumRecord::getPolicyNo, policyNo);
        }
        if (StringUtils.hasText(paymentStatus)) {
            wrapper.eq(PremiumRecord::getPaymentStatus, paymentStatus);
        }
        wrapper.orderByDesc(PremiumRecord::getCreateTime);
        return this.page(page, wrapper);
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
            return this.updateById(record);
        }
        return false;
    }
    
    public List<PremiumRecord> findPendingPayments() {
        return this.list(new QueryWrapper<PremiumRecord>()
                .eq("payment_status", "PENDING")
                .orderByAsc("create_time"));
    }
}
