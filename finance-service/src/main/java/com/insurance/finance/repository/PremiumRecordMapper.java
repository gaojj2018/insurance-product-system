package com.insurance.finance.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.finance.entity.PremiumRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PremiumRecordMapper extends BaseMapper<PremiumRecord> {
}
