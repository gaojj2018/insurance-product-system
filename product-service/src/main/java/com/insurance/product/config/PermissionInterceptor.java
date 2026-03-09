package com.insurance.product.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限拦截器
 * 从请求头获取用户权限，校验是否有访问权限
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";

    /**
     * 管理员角色（拥有所有权限）
     */
    private static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList("ADMIN", "SUPER_ADMIN"));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String userId = request.getHeader(HEADER_USER_ID);
        String permissionsStr = request.getHeader(HEADER_USER_PERMISSIONS);
        String userRole = request.getHeader("X-User-Role");

        // 记录请求信息（用于调试）
        log.debug("请求: {}, 用户ID: {}, 角色: {}, 权限: {}",
                request.getRequestURI(), userId, userRole, permissionsStr);

        // 管理员直接放行
        if (userRole != null && ADMIN_ROLES.contains(userRole.toUpperCase())) {
            return true;
        }

        // TODO: 根据具体业务需求，在此处添加权限校验逻辑
        // 可以配合 @RequirePermission 注解使用

        return true;
    }
}
