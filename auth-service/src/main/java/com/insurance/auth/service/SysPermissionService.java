package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysPermission;
import com.insurance.auth.repository.SysPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysPermissionService {
    
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    
    public Page<SysPermission> findPage(int pageNum, int pageSize, String keyword) {
        Page<SysPermission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysPermission::getPermissionName, keyword)
                   .or().like(SysPermission::getPermissionCode, keyword);
        }
        wrapper.orderByAsc(SysPermission::getSortOrder);
        return sysPermissionMapper.selectPage(page, wrapper);
    }
    
    public List<SysPermission> findAll() {
        return sysPermissionMapper.selectList(null);
    }
    
    public List<SysPermission> findTree() {
        List<SysPermission> all = sysPermissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSortOrder)
        );
        return buildTree(all, 0L);
    }
    
    public List<SysPermission> findByRoleId(Long roleId) {
        return sysPermissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, roleId)
        );
    }
    
    private List<SysPermission> buildTree(List<SysPermission> list, Long parentId) {
        return list.stream()
            .filter(item -> item.getParentId().equals(parentId))
            .peek(item -> item.setChildren(buildTree(list, item.getId())))
            .toList();
    }
    
    public SysPermission getById(Long id) {
        return sysPermissionMapper.selectById(id);
    }
    
    public SysPermission create(SysPermission permission) {
        sysPermissionMapper.insert(permission);
        return permission;
    }
    
    public boolean update(SysPermission permission) {
        return sysPermissionMapper.updateById(permission) > 0;
    }
    
    public boolean delete(Long id) {
        // Check if has children
        long count = sysPermissionMapper.selectCount(
            new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id)
        );
        if (count > 0) {
            return false;
        }
        return sysPermissionMapper.deleteById(id) > 0;
    }
    
    public List<String> getPermissionsByRoleIds(List<Long> roleIds) {
        // Return all permission codes for the given roles
        List<SysPermission> permissions = sysPermissionMapper.selectList(null);
        return permissions.stream()
            .map(SysPermission::getPermissionCode)
            .toList();
    }
}
