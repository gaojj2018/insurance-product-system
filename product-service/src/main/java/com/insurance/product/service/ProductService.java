package com.insurance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.product.dto.ProductQueryDTO;
import com.insurance.product.entity.Product;
import com.insurance.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 产品Service
 */
@Service
public class ProductService extends ServiceImpl<ProductRepository, Product> {
    
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
}
