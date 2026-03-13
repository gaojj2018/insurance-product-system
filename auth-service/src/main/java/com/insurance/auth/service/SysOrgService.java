package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysOrg;
import com.insurance.auth.repository.SysOrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysOrgService {
    
    @Autowired
    private SysOrgMapper sysOrgMapper;
    
    public Page<SysOrg> findPage(int pageNum, int pageSize, String keyword) {
        Page<SysOrg> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysOrg::getOrgName, keyword)
                   .or().like(SysOrg::getOrgCode, keyword);
        }
        wrapper.orderByAsc(SysOrg::getSortOrder);
        return sysOrgMapper.selectPage(page, wrapper);
    }
    
    public List<SysOrg> findAll() {
        return sysOrgMapper.selectList(null);
    }
    
    public List<SysOrg> findTree() {
        List<SysOrg> all = sysOrgMapper.selectList(
            new LambdaQueryWrapper<SysOrg>().orderByAsc(SysOrg::getSortOrder)
        );
        return buildTree(all, 0L);
    }
    
    private List<SysOrg> buildTree(List<SysOrg> list, Long parentId) {
        return list.stream()
            .filter(item -> {
                Long itemParentId = item.getParentId();
                if (parentId == null) {
                    return itemParentId == null || itemParentId.equals(0L);
                }
                return parentId.equals(itemParentId);
            })
            .peek(item -> item.setChildren(buildTree(list, item.getId())))
            .toList();
    }
    
    public SysOrg getById(Long id) {
        return sysOrgMapper.selectById(id);
    }
    
    public SysOrg create(SysOrg org) {
        if (org.getOrgCode() == null || org.getOrgCode().isEmpty()) {
            // Find max org code - query all non-deleted orgs and find max numerically
            LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysOrg::getDeleted, 0);
            List<SysOrg> allOrgs = sysOrgMapper.selectList(wrapper);
            
            int maxNum = 0;
            for (SysOrg o : allOrgs) {
                String code = o.getOrgCode();
                if (code != null && code.startsWith("ORG")) {
                    try {
                        int num = Integer.parseInt(code.substring(3));
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            org.setOrgCode("ORG" + String.format("%03d", maxNum + 1));
        }
        
        if (org.getParentId() != null && org.getParentId() > 0) {
            SysOrg parent = sysOrgMapper.selectById(org.getParentId());
            if (parent != null) {
                String parentPath = parent.getParentPath();
                org.setParentPath(parentPath != null ? parentPath + "/" + org.getParentId() : "/0/" + org.getParentId());
            } else {
                org.setParentPath("/0");
            }
        } else {
            org.setParentPath("/0");
        }
        
        sysOrgMapper.insert(org);
        return org;
    }
    
    public boolean update(SysOrg org) {
        return sysOrgMapper.updateById(org) > 0;
    }
    
    public boolean delete(Long id) {
        // Check if has children
        long count = sysOrgMapper.selectCount(
            new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, id)
        );
        if (count > 0) {
            return false;
        }
        return sysOrgMapper.deleteById(id) > 0;
    }
}
