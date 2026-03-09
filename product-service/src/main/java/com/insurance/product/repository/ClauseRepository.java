package com.insurance.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.product.entity.Clause;
import org.apache.ibatis.annotations.Mapper;

/**
 * 条款Repository
 */
@Mapper
public interface ClauseRepository extends BaseMapper<Clause> {
}
