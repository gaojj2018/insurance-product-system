package com.insurance.application.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品DTO - 用于服务间调用
 */
@Data
public class ProductDTO {

    private Long productId;
    private String productCode;
    private String productName;
    private String productType;
    private String insuranceType;
    private String coveragePeriod;
    private String paymentPeriod;
    private BigDecimal minCoverage;
    private BigDecimal maxCoverage;
    private String status;
    private String description;
}
