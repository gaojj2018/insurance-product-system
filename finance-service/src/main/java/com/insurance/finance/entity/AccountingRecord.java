package com.insurance.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("accounting_record")
public class AccountingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String policyNo;
    private String applicationNo;
    private String productCode;
    
    private String accountType;
    private String accountSubject;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    
    private String transactionType;
    private String transactionNo;
    private LocalDateTime transactionDate;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
