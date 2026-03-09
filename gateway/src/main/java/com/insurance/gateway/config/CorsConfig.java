package com.insurance.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // 生产环境应配置具体域名，通过环境变量 CORS_ALLOWED_ORIGINS 设置
        // 开发环境默认允许 localhost 相关域名
        String allowedOrigins = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS",
                "http://localhost,http://localhost:5173,http://localhost:80,http://127.0.0.1,http://127.0.0.1:5173");
        corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // 允许的HTTP方法
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头
        corsConfig.setAllowedHeaders(List.of("*"));

        // 允许携带认证信息（cookies）
        corsConfig.setAllowCredentials(true);

        // 暴露的响应头（前端可以获取）
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Id",
                "X-User-Name",
                "X-User-Role"
        ));

        // 预检请求缓存时间（秒）
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
