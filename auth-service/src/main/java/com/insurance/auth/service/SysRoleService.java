package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysRole;
import com.insurance.auth.repository.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleService {
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    public Page<SysRole> findPage(int pageNum, int pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return sysRoleMapper.selectPage(page, null);
    }
    
    public List<SysRole> findAll() {
        return sysRoleMapper.selectList(null);
    }
    
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }
    
    public SysRole create(SysRole role) {
        sysRoleMapper.insert(role);
        return role;
    }
    
    public boolean update(SysRole role) {
        return sysRoleMapper.updateById(role) > 0;
    }
    
    public boolean delete(Long id) {
        return sysRoleMapper.deleteById(id) > 0;
    }
}
