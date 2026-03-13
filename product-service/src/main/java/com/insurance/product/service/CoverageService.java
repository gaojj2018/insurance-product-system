package com.insurance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.product.entity.Coverage;
import com.insurance.product.repository.CoverageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 险种Service
 */
@Service
public class CoverageService extends ServiceImpl<CoverageRepository, Coverage> {
    
    /**
     * 分页查询险种
     */
    public IPage<Coverage> queryPage(Integer pageNum, Integer pageSize, Long productId) {
        Page<Coverage> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Coverage> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(Coverage::getProductId, productId);
        }
        
        wrapper.orderByDesc(Coverage::getCreatedTime);
        
        return this.page(page, wrapper);
    }
    
    /**
     * 根据产品ID查询险种列表
     */
    public List<Coverage> listByProductId(Long productId) {
        LambdaQueryWrapper<Coverage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coverage::getProductId, productId);
        wrapper.in(Coverage::getStatus, "DRAFT", "ACTIVE");
        wrapper.orderByAsc(Coverage::getCoverageId);
        return this.list(wrapper);
    }
    
    /**
     * 创建险种
     */
    public boolean createCoverage(Coverage coverage) {
        coverage.setStatus("DRAFT");
        return this.save(coverage);
    }
    
    /**
     * 更新险种状态
     */
    public boolean updateStatus(Long coverageId, String status) {
        Coverage coverage = this.getById(coverageId);
        if (coverage == null) {
            return false;
        }
        coverage.setStatus(status);
        return this.updateById(coverage);
    }
}
