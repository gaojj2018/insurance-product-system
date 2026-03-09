package com.insurance.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
