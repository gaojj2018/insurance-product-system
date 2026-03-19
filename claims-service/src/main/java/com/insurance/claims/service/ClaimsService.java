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

/**
 * 理赔Service - 提供理赔业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class ClaimsService {
    
    private final ClaimsRepository claimsRepository;
    private final FinanceClient financeClient;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    public long count() {
        return claimsRepository.selectCount(null);
    }
    
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
    
    public IPage<Claims> getPage(int pageNum, int pageSize, String claimNo, String policyNo, String claimantName, String status) {
        Page<Claims> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Claims> wrapper = new LambdaQueryWrapper<>();
        
        if (claimNo != null && !claimNo.isEmpty()) {
            wrapper.like(Claims::getClaimsNo, claimNo);
        }
        if (policyNo != null && !policyNo.isEmpty()) {
            wrapper.like(Claims::getPolicyNo, policyNo);
        }
        if (claimantName != null && !claimantName.isEmpty()) {
            wrapper.like(Claims::getApplicantName, claimantName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Claims::getStatus, status);
        }
        
        wrapper.orderByDesc(Claims::getCreatedTime);
        
        IPage<Claims> result = claimsRepository.selectPage(page, wrapper);
        
        long total = claimsRepository.selectCount(wrapper);
        result.setTotal(total);
        
        return result;
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
    
    @Transactional
    public void processSettlement(Long id) {
        Claims claims = claimsRepository.selectById(id);
        if (claims != null && "APPROVED".equals(claims.getStatus())) {
            claims.setStatus("SETTLED");
            claims.setSettleTime(LocalDateTime.now());
            claimsRepository.updateById(claims);
        }
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalClaims = claimsRepository.selectCount(null);
        
        LambdaQueryWrapper<Claims> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.in(Claims::getStatus, "REPORTED", "PROCESSING");
        long pendingClaims = claimsRepository.selectCount(pendingWrapper);
        
        LambdaQueryWrapper<Claims> approvedWrapper = new LambdaQueryWrapper<>();
        approvedWrapper.eq(Claims::getStatus, "APPROVED");
        long approvedClaims = claimsRepository.selectCount(approvedWrapper);
        
        LambdaQueryWrapper<Claims> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(Claims::getStatus, "PAID");
        long paidClaims = claimsRepository.selectCount(paidWrapper);
        
        LambdaQueryWrapper<Claims> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(Claims::getStatus, "REJECTED");
        long rejectedClaims = claimsRepository.selectCount(rejectedWrapper);
        
        LambdaQueryWrapper<Claims> allWrapper = new LambdaQueryWrapper<>();
        List<Claims> allClaims = claimsRepository.selectList(allWrapper);
        BigDecimal totalClaimAmount = allClaims.stream()
                .map(c -> c.getClaimAmount() != null ? c.getClaimAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalClaims", totalClaims);
        stats.put("pendingClaims", pendingClaims);
        stats.put("approvedClaims", approvedClaims);
        stats.put("paidClaims", paidClaims);
        stats.put("rejectedClaims", rejectedClaims);
        stats.put("totalClaimAmount", totalClaimAmount);
        
        return stats;
    }
}
