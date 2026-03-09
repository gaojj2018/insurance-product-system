package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.insurance.auth.config.JwtTokenProvider;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.repository.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private AuthService authService = new AuthService();

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "baseMapper", sysUserMapper);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "jwtTokenProvider", jwtTokenProvider);

        testUser = new SysUser();
        testUser.setId(3L);
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setRealName("测试用户");
        testUser.setRole("ADMIN");
        testUser.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("登录 - 成功")
    void testLogin_Success() {
        // 模拟 getOne 方法的调用
        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(eq("testuser"), eq(1L), eq("ADMIN"), anyList()))
                .thenReturn("mock.jwt.token");

        Map<String, Object> result = authService.login("testuser", "password123");

        assertTrue((Boolean) result.get("success"));
        assertEquals("mock.jwt.token", result.get("token"));
        assertEquals("testuser", result.get("username"));
        assertEquals("ADMIN", result.get("role"));
        assertNotNull(result.get("permissions"));
    }

    @Test
    @DisplayName("登录 - 用户不存在")
    void testLogin_UserNotFound() {
        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(null);

        Map<String, Object> result = authService.login("nonexistent", "password");

        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或密码错误", result.get("message"));
    }

    @Test
    @DisplayName("登录 - 密码错误")
    void testLogin_WrongPassword() {
        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        Map<String, Object> result = authService.login("testuser", "wrongpassword");

        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或密码错误", result.get("message"));
    }

    @Test
    @DisplayName("注册 - 成功")
    void testRegister_Success() {
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole("AGENT");

        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        SysUser result = authService.register(newUser);

        assertNotNull(result);
        assertEquals("encoded_password", result.getPassword());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    @DisplayName("注册 - 用户名已存在")
    void testRegister_UsernameExists() {
        SysUser newUser = new SysUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password123");

        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> authService.register(newUser));
    }

    @Test
    @DisplayName("验证Token - 有效")
    void testValidateToken_Valid() {
        when(jwtTokenProvider.validateToken("valid.token")).thenReturn(true);

        boolean result = authService.validateToken("valid.token");

        assertTrue(result);
    }

    @Test
    @DisplayName("验证Token - 无效")
    void testValidateToken_Invalid() {
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        boolean result = authService.validateToken("invalid.token");

        assertFalse(result);
    }

    @Test
    @DisplayName("获取默认权限 - ADMIN角色")
    void testGetDefaultPermissions_Admin() {
        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyLong(), anyString(), anyList()))
                .thenReturn("token");

        Map<String, Object> result = authService.login("testuser", "password");

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) result.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.contains("system"));
        assertTrue(permissions.contains("product"));
    }

    @Test
    @DisplayName("获取默认权限 - AGENT角色")
    void testGetDefaultPermissions_Agent() {
        testUser.setRole("AGENT");
        when(sysUserMapper.selectOne(any(QueryWrapper.class), anyBoolean())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyLong(), anyString(), anyList()))
                .thenReturn("token");

        Map<String, Object> result = authService.login("testuser", "password");

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) result.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.contains("product:view"));
        assertTrue(permissions.contains("application:create"));
        assertFalse(permissions.contains("system"));
    }
}
