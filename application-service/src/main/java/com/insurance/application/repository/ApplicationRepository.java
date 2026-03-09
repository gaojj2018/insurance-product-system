package com.insurance.application.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.application.entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationRepository extends BaseMapper<Application> {
}
