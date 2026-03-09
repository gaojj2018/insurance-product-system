package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysOrg;
import com.insurance.auth.entity.SysRole;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.repository.SysOrgMapper;
import com.insurance.auth.repository.SysRoleMapper;
import com.insurance.auth.repository.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysUserService {
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysOrgMapper sysOrgMapper;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Page<SysUser> findPage(int pageNum, int pageSize, String keyword, Long orgId) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUsername, keyword)
                   .or().like(SysUser::getRealName, keyword)
                   .or().like(SysUser::getMobile, keyword);
        }
        if (orgId != null) {
            wrapper.eq(SysUser::getOrgId, orgId);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return sysUserMapper.selectPage(page, wrapper);
    }
    
    public List<SysUser> findAll() {
        return sysUserMapper.selectList(null);
    }
    
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user != null && user.getOrgId() != null) {
            SysOrg org = sysOrgMapper.selectById(user.getOrgId());
            if (org != null) {
                user.setOrgName(org.getOrgName());
            }
        }
        return user;
    }
    
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
    }
    
    public SysUser create(SysUser user) {
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        sysUserMapper.insert(user);
        return user;
    }
    
    public boolean update(SysUser user) {
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", user.getId());
        
        if (StringUtils.hasText(user.getUsername())) {
            wrapper.set("username", user.getUsername());
        }
        if (StringUtils.hasText(user.getRealName())) {
            wrapper.set("real_name", user.getRealName());
        }
        if (StringUtils.hasText(user.getMobile())) {
            wrapper.set("mobile", user.getMobile());
        }
        if (StringUtils.hasText(user.getEmail())) {
            wrapper.set("email", user.getEmail());
        }
        if (StringUtils.hasText(user.getRole())) {
            wrapper.set("role", user.getRole());
        }
        if (user.getOrgId() != null) {
            wrapper.set("org_id", user.getOrgId());
        }
        if (StringUtils.hasText(user.getStatus())) {
            wrapper.set("status", user.getStatus());
        }
        if (StringUtils.hasText(user.getPassword())) {
            wrapper.set("password", passwordEncoder.encode(user.getPassword()));
        }
        
        return sysUserMapper.update(null, wrapper) > 0;
    }
    
    public boolean delete(Long id) {
        return sysUserMapper.deleteById(id) > 0;
    }
    
    public boolean updateStatus(Long id, String status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        return sysUserMapper.updateById(user) > 0;
    }
    
    public boolean resetPassword(Long id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return sysUserMapper.updateById(user) > 0;
    }
    
    public SysOrg getOrgById(Long orgId) {
        return sysOrgMapper.selectById(orgId);
    }
    
    public SysRole getRoleById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }
}
