package com.insurance.auth.config;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtTokenProvider 单元测试
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 设置测试用的密钥（至少256位/32字符）
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);
    }

    @Test
    @DisplayName("生成Token - 成功")
    void testGenerateToken() {
        // Given
        String username = "testuser";
        Long userId = 1L;
        String role = "ADMIN";
        List<String> permissions = Arrays.asList("product:view", "product:create");

        // When
        String token = jwtTokenProvider.generateToken(username, userId, role, permissions);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @DisplayName("解析Token - 获取用户名")
    void testParseToken_GetUsername() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", 1L, "ADMIN", List.of("product:view"));

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("解析Token - 获取权限列表")
    void testParseToken_GetPermissions() {
        // Given
        List<String> permissions = Arrays.asList("product:view", "product:create", "product:edit");
        String token = jwtTokenProvider.generateToken("testuser", 1L, "ADMIN", permissions);

        // When
        List<String> result = jwtTokenProvider.getPermissionsFromToken(token);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("product:view"));
        assertTrue(result.contains("product:create"));
    }

    @Test
    @DisplayName("验证Token - 有效Token")
    void testValidateToken_Valid() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", 1L, "ADMIN", List.of("product:view"));

        // When
        boolean result = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("验证Token - 无效Token")
    void testValidateToken_Invalid() {
        // When
        boolean result = jwtTokenProvider.validateToken("invalid.token.here");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("验证Token - 空Token")
    void testValidateToken_Empty() {
        // When
        boolean result = jwtTokenProvider.validateToken("");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("验证Token - null Token")
    void testValidateToken_Null() {
        // When
        boolean result = jwtTokenProvider.validateToken(null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("解析Token - 获取Claims")
    void testParseToken() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", 1L, "ADMIN", List.of("product:view"));

        // When
        Claims claims = jwtTokenProvider.parseToken(token);

        // Then
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1, ((Number) claims.get("userId")).intValue());
        assertEquals("ADMIN", claims.get("role"));
    }
}
