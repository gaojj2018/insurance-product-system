package com.insurance.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("premium_record")
public class PremiumRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String policyNo;
    private String applicationNo;
    private String productCode;
    private String productName;
    private String insuredName;
    
    private BigDecimal premium;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String paymentBatchNo;
    
    private String invoiceNo;
    private String invoiceStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
