package com.insurance.policy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("policy")
public class Policy {
    
    @TableId(type = IdType.AUTO)
    private Long policyId;
    
    private String policyNo;
    
    private Long applicationId;
    
    private String applicationNo;
    
    private Long productId;
    
    private String productName;
    
    private String productCode;
    
    private Long applicantId;
    
    private String applicantName;
    
    private Long insuredId;
    
    private BigDecimal coverage;
    
    private BigDecimal premium;
    
    private String coveragePeriod;
    
    private String paymentPeriod;
    
    private String paymentMethod;
    
    /**
     * 保单状态: EFFECTIVE-有效, TERMINATED-终止, EXPIRED-满期, SURRENDERED-退保, LAPSED-失效
     */
    private String status;
    
    private LocalDateTime issueDate;
    
    private LocalDateTime effectiveDate;
    
    private LocalDateTime expirationDate;
    
    private LocalDateTime nextPaymentDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
