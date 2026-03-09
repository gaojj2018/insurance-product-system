package com.insurance.policy.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.policy.entity.Policy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PolicyRepository extends BaseMapper<Policy> {
}
