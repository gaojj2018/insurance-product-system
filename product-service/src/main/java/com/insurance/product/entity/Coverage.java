package com.insurance.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 险种实体类
 */
@Data
@TableName("coverage")
public class Coverage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "coverage_id", type = IdType.AUTO)
    private Long coverageId;
    
    private Long productId;
    
    private String coverageCode;
    
    private String coverageName;
    
    private String coverageType;
    
    private String coverageKind;
    
    private BigDecimal minSumInsured;
    
    private BigDecimal maxSumInsured;
    
    private BigDecimal basePremiumRate;
    
    private String calculationType;
    
    private String status;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
