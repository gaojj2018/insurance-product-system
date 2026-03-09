package com.insurance.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 条款实体类
 */
@Data
@TableName("clause")
public class Clause implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "clause_id", type = IdType.AUTO)
    private Long clauseId;
    
    private Long productId;
    
    private String clauseCode;
    
    private String clauseName;
    
    private String clauseType;
    
    private String clauseContent;
    
    private Integer clauseOrder;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
