package com.insurance.auth.service;

import com.insurance.auth.entity.SysRole;
import com.insurance.auth.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class DataScopeService {

    public static final String DATA_SCOPE_ALL = "ALL";
    public static final String DATA_SCOPE_DEPT_ONLY = "DEPT_ONLY";
    public static final String DATA_SCOPE_DEPT_AND_BELOW = "DEPT_AND_BELOW";
    public static final String DATA_SCOPE_SELF = "SELF";

    @Autowired
    private UserPermissionService userPermissionService;

    public String getDataScope(HttpServletRequest request) {
        String dataScope = (String) request.getAttribute("dataScope");
        return dataScope != null ? dataScope : DATA_SCOPE_ALL;
    }

    public Long getDataScopeOrgId(HttpServletRequest request) {
        Object orgId = request.getAttribute("dataScopeOrgId");
        return orgId != null ? (Long) orgId : null;
    }

    public String getDataScope(SysUser currentUser) {
        if (currentUser == null || currentUser.getOrgId() == null) {
            return DATA_SCOPE_ALL;
        }
        List<SysRole> roles = userPermissionService.getRolesByUserId(currentUser.getId());
        if (roles.isEmpty()) {
            return DATA_SCOPE_ALL;
        }

        for (SysRole role : roles) {
            if (role.getDataScope() != null) {
                if (DATA_SCOPE_SELF.equals(role.getDataScope())) {
                    return DATA_SCOPE_SELF;
                }
            }
        }

        for (SysRole role : roles) {
            if (role.getDataScope() != null) {
                if (DATA_SCOPE_DEPT_ONLY.equals(role.getDataScope())) {
                    return DATA_SCOPE_DEPT_ONLY;
                }
            }
        }

        for (SysRole role : roles) {
            if (role.getDataScope() != null) {
                if (DATA_SCOPE_DEPT_AND_BELOW.equals(role.getDataScope())) {
                    return DATA_SCOPE_DEPT_AND_BELOW;
                }
            }
        }

        return DATA_SCOPE_ALL;
    }

    public Long getDataScopeOrgId(SysUser currentUser) {
        return currentUser != null ? currentUser.getOrgId() : null;
    }
}
