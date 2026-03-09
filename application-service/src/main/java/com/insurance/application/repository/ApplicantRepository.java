package com.insurance.application.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.application.entity.Applicant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicantRepository extends BaseMapper<Applicant> {
}
