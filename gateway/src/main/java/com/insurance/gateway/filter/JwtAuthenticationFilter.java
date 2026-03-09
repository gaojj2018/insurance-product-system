package com.insurance.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器
 * 全局过滤器，在所有请求到达下游服务前进行Token验证
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 白名单路径 - 不需要认证的接口
     */
    private static final List<String> WHITE_LIST = List.of(
            "/api/auth/login",           // 登录接口
            "/api/auth/register",        // 注册接口
            "/api/auth/validate",        // Token验证接口
            "/actuator/**",              // 监控端点
            "/eureka/**",                // Eureka端点
            "/error"                     // 错误页面
    );

    /**
     * OPTIONS请求直接放行（CORS预检）
     */
    private static final String HTTP_METHOD_OPTIONS = "OPTIONS";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // OPTIONS请求直接放行
        if (HTTP_METHOD_OPTIONS.equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        // 检查是否在白名单中
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取Authorization头
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 检查Authorization头是否存在
        if (!StringUtils.hasText(authHeader)) {
            return unauthorized(exchange, "缺少认证令牌");
        }

        // 检查Bearer前缀
        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "认证令牌格式错误");
        }

        // 提取Token
        String token = authHeader.substring(7);

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "认证令牌无效或已过期");
        }

        // Token验证通过，将用户信息添加到请求头，传递给下游服务
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            List<String> permissions = jwtUtil.getPermissionsFromToken(token);

            // 构建新的请求，添加用户信息到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Name", username)
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-Permissions", String.join(",", permissions))
                    .header("X-Auth-Token", token)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return unauthorized(exchange, "解析认证令牌失败: " + e.getMessage());
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitePath(String path) {
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpStatus.UNAUTHORIZED.value());
        result.put("message", message);
        result.put("data", null);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 优先级最高，确保在其他过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
