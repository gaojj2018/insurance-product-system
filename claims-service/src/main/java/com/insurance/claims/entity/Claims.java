package com.insurance.claims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("claims")
public class Claims {
    
    @TableId(type = IdType.AUTO)
    private Long claimsId;
    
    private String claimsNo;
    
    private Long policyId;
    
    private String policyNo;
    
    private Long applicantId;
    
    private String applicantName;
    
    private String accidentType;
    
    private LocalDateTime accidentDate;
    
    private String accidentLocation;
    
    private String accidentDesc;
    
    private BigDecimal claimAmount;
    
    private BigDecimal approvedAmount;
    
    private String status;
    
    private String handler;
    
    private LocalDateTime handleTime;
    
    private String payAccount;
    
    private LocalDateTime payTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
