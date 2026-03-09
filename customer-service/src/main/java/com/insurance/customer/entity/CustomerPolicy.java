package com.insurance.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer_policy")
public class CustomerPolicy {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    private String policyNo;
    private String productCode;
    private String productName;
    private String policyStatus;
    private String effectiveDate;
    private String expiryDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
