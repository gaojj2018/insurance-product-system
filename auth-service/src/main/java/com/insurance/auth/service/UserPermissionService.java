package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insurance.auth.entity.*;
import com.insurance.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPermissionService {
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private SysPermissionMapper permissionMapper;
    
    public List<SysRole> getRolesByUserId(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
            .map(SysUserRole::getRoleId)
            .collect(Collectors.toList());
        
        return roleMapper.selectBatchIds(roleIds);
    }
    
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
            .map(SysUserRole::getRoleId)
            .collect(Collectors.toList());
        
        List<SysRolePermission> rolePerms = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, roleIds)
        );
        
        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> permIds = rolePerms.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
        
        return permissionMapper.selectBatchIds(permIds);
    }
    
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<SysPermission> permissions = getPermissionsByUserId(userId);
        return permissions.stream()
            .map(SysPermission::getPermissionCode)
            .collect(Collectors.toList());
    }
    
    public boolean assignRole(Long userId, Long roleId) {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(userId);
        ur.setRoleId(roleId);
        return userRoleMapper.insert(ur) > 0;
    }
    
    public boolean removeRole(Long userId, Long roleId) {
        return userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId)
        ) > 0;
    }
    
    public boolean assignPermission(Long roleId, Long permissionId) {
        SysRolePermission rp = new SysRolePermission();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        return rolePermissionMapper.insert(rp) > 0;
    }
    
    public boolean removePermission(Long roleId, Long permissionId) {
        return rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
                .eq(SysRolePermission::getPermissionId, permissionId)
        ) > 0;
    }
    
    /**
     * Get permissions by role ID
     */
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        List<SysRolePermission> rolePerms = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
        );
        
        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> permIds = rolePerms.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
        
        return permissionMapper.selectBatchIds(permIds);
    }
    
    /**
     * Assign multiple permissions to a role (replace all)
     */
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // First remove all existing permissions for this role
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
        );
        
        // Then add new permissions
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rolePermissionMapper.insert(rp);
        }
        
        return true;
    }
}
