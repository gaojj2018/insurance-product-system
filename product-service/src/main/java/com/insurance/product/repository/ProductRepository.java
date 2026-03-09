package com.insurance.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品Repository
 */
@Mapper
public interface ProductRepository extends BaseMapper<Product> {
}
