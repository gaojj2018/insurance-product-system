package com.insurance.claims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.claims.entity.Claims;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClaimsRepository extends BaseMapper<Claims> {
}
