package com.insurance.claims.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("claims")
public class Claims {
    
    @TableId(type = IdType.AUTO)
    @JsonProperty("claimsId")
    private Long claimsId;
    
    @JsonProperty("claimNo")
    private String claimsNo;
    
    private Long policyId;
    
    private String policyNo;
    
    private Long applicantId;
    
    @JsonProperty("claimantName")
    private String applicantName;
    
    private String accidentType;
    
    private LocalDateTime accidentDate;
    
    private String accidentLocation;
    
    private String accidentDesc;
    
    @JsonProperty("claimAmount")
    private BigDecimal claimAmount;
    
    @JsonProperty("approveAmount")
    private BigDecimal approvedAmount;
    
    @JsonProperty("claimStatus")
    private String status;
    
    private String handler;
    
    private LocalDateTime handleTime;
    
    private String payAccount;
    
    private LocalDateTime payTime;
    
    private LocalDateTime settleTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
