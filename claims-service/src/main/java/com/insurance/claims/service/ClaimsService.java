package com.insurance.claims.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.claims.client.FinanceClient;
import com.insurance.claims.entity.Claims;
import com.insurance.claims.repository.ClaimsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClaimsService {
    
    private final ClaimsRepository claimsRepository;
    private final FinanceClient financeClient;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    @Transactional
    public Claims createClaims(Long policyId, String policyNo, Long applicantId, String applicantName,
                               String accidentType, LocalDateTime accidentDate, String accidentLocation,
                               String accidentDesc, BigDecimal claimAmount) {
        Claims claims = new Claims();
        claims.setClaimsNo("C" + LocalDateTime.now().format(FORMATTER) + String.format("%04d", RANDOM.nextInt(10000)));
        claims.setPolicyId(policyId);
        claims.setPolicyNo(policyNo);
        claims.setApplicantId(applicantId);
        claims.setApplicantName(applicantName);
        claims.setAccidentType(accidentType);
        claims.setAccidentDate(accidentDate);
        claims.setAccidentLocation(accidentLocation);
        claims.setAccidentDesc(accidentDesc);
        claims.setClaimAmount(claimAmount);
        claims.setStatus("REPORTED");
        claimsRepository.insert(claims);
        return claims;
    }
    
    public IPage<Claims> getPage(int pageNum, int pageSize, String status) {
        Page<Claims> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Claims::getStatus, status);
        }
        wrapper.orderByDesc(Claims::getCreatedTime);
        return claimsRepository.selectPage(page, wrapper);
    }
    
    public Claims getById(Long id) {
        return claimsRepository.selectById(id);
    }
    
    public Claims getByClaimsNo(String claimsNo) {
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Claims::getClaimsNo, claimsNo);
        return claimsRepository.selectOne(wrapper);
    }
    
    @Transactional
    public Claims approve(Long id, BigDecimal approvedAmount, String handler, String opinion) {
        Claims claims = claimsRepository.selectById(id);
        if (claims != null) {
            claims.setApprovedAmount(approvedAmount);
            claims.setStatus("APPROVED");
            claims.setHandler(handler);
            claims.setHandleTime(LocalDateTime.now());
            claimsRepository.updateById(claims);
        }
        return claims;
    }
    
    @Transactional
    public Claims pay(Long id, String payAccount) {
        Claims claims = claimsRepository.selectById(id);
        if (claims != null) {
            claims.setStatus("PAID");
            claims.setPayAccount(payAccount);
            claims.setPayTime(LocalDateTime.now());
            claimsRepository.updateById(claims);
            
            // Sync to finance - create accounting record
            try {
                Map<String, Object> record = new HashMap<>();
                record.put("policyNo", claims.getPolicyNo());
                record.put("transactionType", "CLAIM_PAYMENT");
                record.put("amount", claims.getApprovedAmount() != null ? claims.getApprovedAmount() : claims.getClaimAmount());
                record.put("paymentMethod", "BANK_TRANSFER");
                record.put("accountNo", payAccount);
                record.put("transactionStatus", "COMPLETED");
                record.put("remark", "理赔付款 - " + claims.getClaimsNo());
                financeClient.createAccountingRecord(record);
            } catch (Exception e) {
                // Log error but don't fail the payment
                e.printStackTrace();
            }
        }
        return claims;
    }
    
    @Transactional
    public boolean deleteClaims(Long id) {
        return claimsRepository.deleteById(id) > 0;
    }
    
    @Transactional
    public Claims reject(Long id, String handler, String reason) {
        Claims claims = claimsRepository.selectById(id);
        if (claims != null) {
            claims.setStatus("REJECTED");
            claims.setHandler(handler);
            claims.setHandleTime(LocalDateTime.now());
            claimsRepository.updateById(claims);
        }
        return claims;
    }
    
    public List<Claims> getByPolicyId(Long policyId) {
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Claims::getPolicyId, policyId);
        wrapper.notIn(Claims::getStatus, "REJECTED", "CLOSED");
        return claimsRepository.selectList(wrapper);
    }
}
