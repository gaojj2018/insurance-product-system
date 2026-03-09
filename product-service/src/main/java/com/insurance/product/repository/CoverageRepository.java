package com.insurance.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.product.entity.Coverage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 险种Repository
 */
@Mapper
public interface CoverageRepository extends BaseMapper<Coverage> {
}
