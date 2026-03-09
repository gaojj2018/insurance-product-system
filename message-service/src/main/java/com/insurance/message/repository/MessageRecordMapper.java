package com.insurance.message.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.message.entity.MessageRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecord> {
}
