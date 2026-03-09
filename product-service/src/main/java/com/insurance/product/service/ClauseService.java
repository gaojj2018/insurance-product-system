package com.insurance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.product.entity.Clause;
import com.insurance.product.repository.ClauseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 条款Service
 */
@Service
public class ClauseService extends ServiceImpl<ClauseRepository, Clause> {
    
    /**
     * 分页查询条款
     */
    public IPage<Clause> queryPage(Integer pageNum, Integer pageSize, Long productId) {
        Page<Clause> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Clause> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(Clause::getProductId, productId);
        }
        
        wrapper.orderByAsc(Clause::getClauseOrder);
        
        return this.page(page, wrapper);
    }
    
    /**
     * 根据产品ID查询条款列表
     */
    public List<Clause> listByProductId(Long productId) {
        LambdaQueryWrapper<Clause> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clause::getProductId, productId);
        wrapper.eq(Clause::getStatus, "ACTIVE");
        wrapper.orderByAsc(Clause::getClauseOrder);
        return this.list(wrapper);
    }
    
    /**
     * 创建条款
     */
    public boolean createClause(Clause clause) {
        clause.setStatus("ACTIVE");
        return this.save(clause);
    }
}
