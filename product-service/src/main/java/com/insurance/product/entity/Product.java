package com.insurance.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 产品实体类
 */
@Data
@TableName("product")
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;
    
    @TableField("product_code")
    private String productCode;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("product_type")
    private String productType;
    
    @TableField("insurance_type")
    private String insuranceType;
    
    @TableField("coverage_period")
    private String coveragePeriod;
    
    @TableField("payment_period")
    private String paymentPeriod;
    
    @TableField("min_coverage")
    private BigDecimal minCoverage;
    
    @TableField("max_coverage")
    private BigDecimal maxCoverage;
    
    @TableField("coverage_period_unit")
    private String coveragePeriodUnit;
    
    @TableField("payment_period_unit")
    private String paymentPeriodUnit;
    
    @TableField("waiting_period")
    private Integer waitingPeriod;
    
    @TableField("waiting_period_unit")
    private String waitingPeriodUnit;
    
    private String status;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
