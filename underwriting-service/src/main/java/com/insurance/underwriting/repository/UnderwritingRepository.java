package com.insurance.underwriting.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.underwriting.entity.Underwriting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnderwritingRepository extends BaseMapper<Underwriting> {
}
