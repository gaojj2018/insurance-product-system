package com.insurance.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("commission_record")
public class CommissionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String policyNo;
    private String applicationNo;
    private String productCode;
    private String productName;
    
    private String agentId;
    private String agentName;
    private String agentLevel;
    
    private BigDecimal premium;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private String commissionStatus;
    private LocalDateTime commissionDate;
    
    private String paymentBatchNo;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
