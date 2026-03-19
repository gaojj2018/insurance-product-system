package com.insurance.underwriting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("underwriting")
public class Underwriting {
    
    @TableId(type = IdType.AUTO)
    private Long underwritingId;
    
    private Long applicationId;
    
    private String applicationNo;
    
    /**
     * 产品名称（冗余字段）
     */
    @TableField("product_name")
    private String productName;
    
    /**
     * 投保人姓名（冗余字段）
     */
    @TableField("applicant_name")
    private String applicantName;
    
    /**
     * 保额（冗余字段，从投保申请获取）
     */
    private BigDecimal coverageAmount;
    
    /**
     * 保费（冗余字段，从投保申请获取）
     */
    private BigDecimal premium;
    
    /**
     * 缴费方式（冗余字段，从投保申请获取）
     */
    private String paymentMethod;
    
    /**
     * 保障期间（冗余字段，从投保申请获取）
     */
    private String coveragePeriod;
    
    /**
     * 核保结果: PASS-通过, DECLINE-拒保, DEFERRED-延期, MANUAL-需人工核保
     */
    private String result;
    
    /**
     * 核保类型: AUTO-自动核保, MANUAL-人工核保
     */
    private String type;
    
    /**
     * 核保等级 (A/B/C/D)
     */
    private String level;
    
    /**
     * 风险评分
     */
    private Integer riskScore;
    
    /**
     * 核保意见
     */
    private String opinion;
    
    /**
     * 核保人 (人工核保时使用)
     */
    private String underwriter;
    
    /**
     * 核保时间
     */
    private LocalDateTime underwritingTime;
    
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
    
    /**
     * 失效时间
     */
    private LocalDateTime expirationTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
