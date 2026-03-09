package com.insurance.auth.component;

import com.insurance.auth.entity.SysRole;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class DataScopeInterceptor implements HandlerInterceptor {

    public static final String DATA_SCOPE_ALL = "ALL";
    public static final String DATA_SCOPE_DEPT_ONLY = "DEPT_ONLY";
    public static final String DATA_SCOPE_DEPT_AND_BELOW = "DEPT_AND_BELOW";
    public static final String DATA_SCOPE_SELF = "SELF";

    @Autowired
    private UserPermissionService userPermissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SysUser currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getOrgId() == null) {
            return true;
        }

        List<SysRole> roles = userPermissionService.getRolesByUserId(currentUser.getId());
        if (roles.isEmpty()) {
            return true;
        }

        String dataScope = DATA_SCOPE_ALL;
        for (SysRole role : roles) {
            if (role.getDataScope() != null) {
                if (DATA_SCOPE_SELF.equals(role.getDataScope())) {
                    dataScope = DATA_SCOPE_SELF;
                    break;
                } else if (DATA_SCOPE_DEPT_ONLY.equals(role.getDataScope())) {
                    if (!DATA_SCOPE_SELF.equals(dataScope)) {
                        dataScope = DATA_SCOPE_DEPT_ONLY;
                    }
                } else if (DATA_SCOPE_DEPT_AND_BELOW.equals(role.getDataScope())) {
                    if (DATA_SCOPE_ALL.equals(dataScope)) {
                        dataScope = DATA_SCOPE_DEPT_AND_BELOW;
                    }
                } else if (DATA_SCOPE_ALL.equals(role.getDataScope())) {
                    dataScope = DATA_SCOPE_ALL;
                    break;
                }
            }
        }

        request.setAttribute("dataScope", dataScope);
        request.setAttribute("dataScopeOrgId", currentUser.getOrgId());

        return true;
    }

    private SysUser getCurrentUser(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        if (user != null) {
            return user;
        }
        String username = request.getAttribute("username") != null 
            ? request.getAttribute("username").toString() 
            : null;
        if (username == null) {
            username = request.getRemoteUser();
        }
        return null;
    }
}
