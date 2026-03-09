package com.insurance.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.auth.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}
