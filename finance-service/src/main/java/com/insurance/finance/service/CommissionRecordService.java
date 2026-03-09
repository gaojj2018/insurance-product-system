package com.insurance.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.finance.entity.CommissionRecord;
import com.insurance.finance.repository.CommissionRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CommissionRecordService extends ServiceImpl<CommissionRecordMapper, CommissionRecord> {
    
    @Transactional
    public CommissionRecord createCommissionRecord(CommissionRecord record) {
        record.setCommissionStatus("PENDING");
        
        if (record.getPremium() != null && record.getCommissionRate() != null) {
            BigDecimal commissionAmount = record.getPremium()
                    .multiply(record.getCommissionRate())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            record.setCommissionAmount(commissionAmount);
        }
        
        this.save(record);
        return record;
    }
    
    public List<CommissionRecord> findByPolicyNo(String policyNo) {
        return this.list(new QueryWrapper<CommissionRecord>()
                .eq("policy_no", policyNo)
                .orderByDesc("create_time"));
    }
    
    public List<CommissionRecord> findByAgentId(String agentId) {
        return this.list(new QueryWrapper<CommissionRecord>()
                .eq("agent_id", agentId)
                .orderByDesc("create_time"));
    }
    
    @Transactional
    public boolean updateCommissionStatus(Long id, String status) {
        CommissionRecord record = this.getById(id);
        if (record != null) {
            record.setCommissionStatus(status);
            return this.updateById(record);
        }
        return false;
    }
    
    public List<CommissionRecord> findPendingCommissions() {
        return this.list(new QueryWrapper<CommissionRecord>()
                .eq("commission_status", "PENDING")
                .orderByAsc("create_time"));
    }
}
