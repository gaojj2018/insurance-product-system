package com.insurance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.product.client.ApplicationClient;
import com.insurance.product.client.PolicyClient;
import com.insurance.product.dto.ProductQueryDTO;
import com.insurance.product.entity.Product;
import com.insurance.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产品Service
 */
@Service
@RequiredArgsConstructor
public class ProductService extends ServiceImpl<ProductRepository, Product> {
    
    private final ApplicationClient applicationClient;
    private final PolicyClient policyClient;
    
    /**
     * 分页查询产品
     */
    public IPage<Product> queryPage(ProductQueryDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getProductCode())) {
            wrapper.like(Product::getProductCode, queryDTO.getProductCode());
        }
        if (StringUtils.hasText(queryDTO.getProductName())) {
            wrapper.like(Product::getProductName, queryDTO.getProductName());
        }
        if (StringUtils.hasText(queryDTO.getProductType())) {
            wrapper.eq(Product::getProductType, queryDTO.getProductType());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Product::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByDesc(Product::getCreatedTime);
        
        return this.page(page, wrapper);
    }
    
    /**
     * 根据产品类型查询产品列表
     */
    public List<Product> listByType(String productType) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getProductType, productType);
        wrapper.eq(Product::getStatus, "ACTIVE");
        wrapper.orderByDesc(Product::getCreatedTime);
        return this.list(wrapper);
    }
    
    /**
     * 创建产品
     */
    public boolean createProduct(Product product) {
        product.setStatus("DRAFT");
        return this.save(product);
    }
    
    /**
     * 更新产品状态
     */
    public boolean updateStatus(Long productId, String status) {
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        product.setStatus(status);
        return this.updateById(product);
    }
    
    /**
     * 发布产品
     */
    public boolean publishProduct(Long productId) {
        return updateStatus(productId, "ACTIVE");
    }
    
    /**
     * 停售产品
     */
    public boolean stopProduct(Long productId) {
        return updateStatus(productId, "INACTIVE");
    }
    
    /**
     * 检查产品是否有业务关联（投保单或保单）
     * 返回关联的业务列表，如果有关联则不能删除
     */
    public List<Map<String, Object>> checkBusinessReferences(Long productId) {
        List<Map<String, Object>> references = new ArrayList<>();
        
        try {
            Map<String, Object> appResponse = applicationClient.getByProductId(productId);
            if (appResponse != null && appResponse.get("code") != null 
                    && (Integer) appResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> applications = (List<Map<String, Object>>) appResponse.get("data");
                if (applications != null && !applications.isEmpty()) {
                    for (Map<String, Object> app : applications) {
                        references.add(Map.of(
                            "type", "APPLICATION",
                            "id", app.get("applicationId"),
                            "no", app.get("applicationNo"),
                            "status", app.get("status"),
                            "message", "投保单 " + app.get("applicationNo") + " (状态: " + app.get("status") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
        }
        
        try {
            Map<String, Object> policyResponse = policyClient.getByProductId(productId);
            if (policyResponse != null && policyResponse.get("code") != null 
                    && (Integer) policyResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> policies = (List<Map<String, Object>>) policyResponse.get("data");
                if (policies != null && !policies.isEmpty()) {
                    for (Map<String, Object> policy : policies) {
                        references.add(Map.of(
                            "type", "POLICY",
                            "id", policy.get("policyId"),
                            "no", policy.get("policyNo"),
                            "status", policy.get("status"),
                            "message", "保单 " + policy.get("policyNo") + " (状态: " + policy.get("status") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        return references;
    }
}
