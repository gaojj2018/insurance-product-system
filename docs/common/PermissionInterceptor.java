package com.insurance.common.interceptor;

import com.insurance.common.annotation.Logical;
import com.insurance.common.annotation.RequirePermission;
import com.insurance.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 权限拦截器
 * 从请求头获取用户权限，校验是否有访问权限
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    // 请求头名称（与Gateway传递的一致）
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非Controller方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission annotation = handlerMethod.getMethodAnnotation(RequirePermission.class);

        // 如果方法上没有注解，检查类上是否有
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }

        // 没有权限注解，直接放行
        if (annotation == null) {
            return true;
        }

        // 获取用户信息
        String userIdStr = request.getHeader(HEADER_USER_ID);
        if (userIdStr == null || userIdStr.isEmpty()) {
            log.warn("未获取到用户信息，拒绝访问: {}", request.getRequestURI());
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 获取用户权限列表
        String permissionsStr = request.getHeader(HEADER_USER_PERMISSIONS);
        List<String> userPermissions = permissionsStr != null && !permissionsStr.isEmpty()
                ? Arrays.asList(permissionsStr.split(","))
                : List.of();

        // 校验权限
        String[] requiredPermissions = annotation.value();
        Logical logical = annotation.logical();

        boolean hasPermission;
        if (logical == Logical.AND) {
            // 需要所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            // 需要任一权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            log.warn("用户[{}]权限不足，需要权限: {}, 拥有权限: {}",
                    userIdStr, Arrays.toString(requiredPermissions), userPermissions);
            throw new BusinessException(403, "权限不足，无法访问");
        }

        return true;
    }
}
