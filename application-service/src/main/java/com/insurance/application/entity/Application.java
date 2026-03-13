package com.insurance.application.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("application")
public class Application {
    
    @TableId(type = IdType.AUTO)
    private Long applicationId;
    
    private String applicationNo;
    
    private Long productId;
    
    private String productName;
    
    private String productCode;
    
    /**
     * 投保人ID
     */
    private Long applicantId;
    
    /**
     * 投保人姓名
     */
    private String applicantName;
    
    /**
     * 被保人ID
     */
    private Long insuredId;
    
    /**
     * 受益人ID
     */
    private Long beneficiaryId;
    
    /**
     * 保额
     */
    private BigDecimal coverage;
    
    /**
     * 保费
     */
    private BigDecimal premium;
    
    /**
     * 投保期限
     */
    private String coveragePeriod;
    
    /**
     * 缴费期限
     */
    private String paymentPeriod;
    
    /**
     * 缴费方式 (年缴/半年缴/季缴/月缴)
     */
    private String paymentMethod;
    
    /**
     * 投保状态 (DRAFT-草稿, SUBMITTED-已提交, UNDERWRITING-核保中, APPROVED-核保通过, REJECTED-核保拒绝, POLICY_ISSUED-已出单)
     */
    private String status;
    
    /**
     * 核保结果ID
     */
    private Long underwritingId;
    
    /**
     * 保单ID (承保后生成)
     */
    private Long policyId;
    
    /**
     * 备注
     */
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
