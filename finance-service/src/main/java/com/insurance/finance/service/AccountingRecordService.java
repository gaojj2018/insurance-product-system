package com.insurance.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.finance.entity.AccountingRecord;
import com.insurance.finance.repository.AccountingRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountingRecordService extends ServiceImpl<AccountingRecordMapper, AccountingRecord> {
    
    @Transactional
    public AccountingRecord createAccountingRecord(AccountingRecord record) {
        this.save(record);
        return record;
    }
    
    public List<AccountingRecord> findByPolicyNo(String policyNo) {
        return this.list(new QueryWrapper<AccountingRecord>()
                .eq("policy_no", policyNo)
                .orderByDesc("transaction_date"));
    }
    
    public List<AccountingRecord> findByTransactionType(String transactionType) {
        return this.list(new QueryWrapper<AccountingRecord>()
                .eq("transaction_type", transactionType)
                .orderByDesc("transaction_date"));
    }
    
    public List<AccountingRecord> findByAccountType(String accountType) {
        return this.list(new QueryWrapper<AccountingRecord>()
                .eq("account_type", accountType)
                .orderByDesc("transaction_date"));
    }
}
