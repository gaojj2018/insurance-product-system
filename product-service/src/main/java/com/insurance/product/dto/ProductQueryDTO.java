package com.insurance.product.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 产品查询DTO
 */
@Data
public class ProductQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String productCode;
    
    private String productName;
    
    private String productType;
    
    private String status;
}
