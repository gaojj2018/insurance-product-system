package com.insurance.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("claim_payment")
public class ClaimPayment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String claimNo;
    private String policyNo;
    private String customerName;
    private BigDecimal claimAmount;
    private String paymentMethod;
    private String accountNo;
    private String paymentStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
