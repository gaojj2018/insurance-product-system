package com.insurance.finance.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.finance.entity.AccountingRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountingRecordMapper extends BaseMapper<AccountingRecord> {
}
