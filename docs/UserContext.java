package com.insurance.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用户上下文工具类
 * 用于下游服务从请求头中获取Gateway传递的用户信息
 * 
 * 使用方式：
 * 1. 将此文件复制到各微服务的 util 包下（如 com.insurance.product.util）
 * 2. 在Controller或Service中调用 UserContext.getCurrentUserId() 等方法获取用户信息
 * 
 * 示例：
 * <pre>
 * Long userId = UserContext.getCurrentUserId();
 * String username = UserContext.getCurrentUsername();
 * List<String> permissions = UserContext.getCurrentPermissions();
 * </pre>
 * 
 * 注意：各微服务需要在Controller中确保能获取到这些请求头，
 * Gateway已通过认证过滤器将用户信息添加到请求头中。
 */
public class UserContext {

    // 请求头名称常量
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";
    public static final String HEADER_AUTH_TOKEN = "X-Auth-Token";

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        String userIdStr = request.getHeader(HEADER_USER_ID);
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_USER_NAME);
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_USER_ROLE);
    }

    /**
     * 获取当前用户权限列表
     */
    public static List<String> getCurrentPermissions() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return Collections.emptyList();
        }
        String permissionsStr = request.getHeader(HEADER_USER_PERMISSIONS);
        if (permissionsStr != null && !permissionsStr.isEmpty()) {
            return Arrays.asList(permissionsStr.split(","));
        }
        return Collections.emptyList();
    }

    /**
     * 获取当前Token
     */
    public static String getCurrentToken() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_AUTH_TOKEN);
    }

    /**
     * 检查当前用户是否具有指定权限
     */
    public static boolean hasPermission(String permission) {
        return getCurrentPermissions().contains(permission);
    }

    /**
     * 检查当前用户是否具有任一指定权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        List<String> currentPermissions = getCurrentPermissions();
        for (String permission : permissions) {
            if (currentPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有指定角色
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return role != null && role.equals(currentRole);
    }

    /**
     * 判断当前用户是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}
