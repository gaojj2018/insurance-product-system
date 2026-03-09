package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysOrg;
import com.insurance.auth.entity.SysRole;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.repository.SysOrgMapper;
import com.insurance.auth.repository.SysRoleMapper;
import com.insurance.auth.repository.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SysUserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SysUserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysOrgMapper sysOrgMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SysUserService sysUserService;

    private SysUser testUser;
    private SysOrg testOrg;
    private SysRole testRole;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("123456");
        testUser.setRealName("管理员");
        testUser.setMobile("13800138000");
        testUser.setEmail("admin@example.com");
        testUser.setRole("ADMIN");
        testUser.setOrgId(1L);
        testUser.setStatus("ACTIVE");

        testOrg = new SysOrg();
        testOrg.setId(1L);
        testOrg.setOrgName("总部");

        testRole = new SysRole();
        testRole.setId(1L);
        testRole.setRoleName("管理员");
        testRole.setRoleCode("ADMIN");
    }

    @Test
    @DisplayName("分页查询用户 - 有关键字")
    void testFindPage_WithKeyword() {
        Page<SysUser> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<SysUser> result = sysUserService.findPage(1, 10, "admin", null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("admin", result.getRecords().get(0).getUsername());
        verify(sysUserMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询用户 - 按机构ID筛选")
    void testFindPage_WithOrgId() {
        Page<SysUser> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<SysUser> result = sysUserService.findPage(1, 10, null, 1L);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(sysUserMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询用户 - 无条件")
    void testFindPage_NoCondition() {
        Page<SysUser> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<SysUser> result = sysUserService.findPage(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("查询所有用户")
    void testFindAll() {
        List<SysUser> expectedUsers = Arrays.asList(testUser);
        when(sysUserMapper.selectList(null)).thenReturn(expectedUsers);

        List<SysUser> result = sysUserService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
        verify(sysUserMapper).selectList(null);
    }

    @Test
    @DisplayName("根据ID查询用户 - 包含机构信息")
    void testGetById_WithOrg() {
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysOrgMapper.selectById(1L)).thenReturn(testOrg);

        SysUser result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("总部", result.getOrgName());
        verify(sysUserMapper).selectById(1L);
        verify(sysOrgMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询用户 - 无机构")
    void testGetById_NoOrg() {
        testUser.setOrgId(null);
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);

        SysUser result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertNull(result.getOrgName());
        verify(sysUserMapper).selectById(1L);
        verify(sysOrgMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("根据用户名查询用户")
    void testGetByUsername() {
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        SysUser result = sysUserService.getByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(sysUserMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("创建用户 - 带密码")
    void testCreate_WithPassword() {
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRealName("新用户");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        SysUser result = sysUserService.create(newUser);

        assertNotNull(result);
        assertEquals("encoded_password", result.getPassword());
        verify(passwordEncoder).encode("password123");
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("创建用户 - 无密码")
    void testCreate_WithoutPassword() {
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setRealName("新用户");

        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        SysUser result = sysUserService.create(newUser);

        assertNotNull(result);
        assertNull(result.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("更新用户 - 带密码")
    void testUpdate_WithPassword() {
        SysUser updateUser = new SysUser();
        updateUser.setId(1L);
        updateUser.setUsername("admin");
        updateUser.setPassword("123456");
        updateUser.setRealName("管理员更新");

        when(passwordEncoder.encode("123456")).thenReturn("encoded_123456");
        when(sysUserMapper.update(any(), any())).thenReturn(1);

        boolean result = sysUserService.update(updateUser);

        assertTrue(result);
        verify(passwordEncoder).encode("123456");
        verify(sysUserMapper).update(any(), any());
    }

    @Test
    @DisplayName("更新用户 - 无密码(保留原密码)")
    void testUpdate_WithoutPassword() {
        SysUser updateUser = new SysUser();
        updateUser.setId(1L);
        updateUser.setUsername("admin");
        updateUser.setRealName("管理员更新");

        when(sysUserMapper.update(any(), any())).thenReturn(1);

        boolean result = sysUserService.update(updateUser);

        assertTrue(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(sysUserMapper).update(any(), any());
    }

    @Test
    @DisplayName("删除用户")
    void testDelete() {
        when(sysUserMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysUserService.delete(1L);

        assertTrue(result);
        verify(sysUserMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除用户 - 不存在")
    void testDelete_NotFound() {
        when(sysUserMapper.deleteById(999L)).thenReturn(0);

        boolean result = sysUserService.delete(999L);

        assertFalse(result);
        verify(sysUserMapper).deleteById(999L);
    }

    @Test
    @DisplayName("更新用户状态")
    void testUpdateStatus() {
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        boolean result = sysUserService.updateStatus(1L, "DISABLED");

        assertTrue(result);
        verify(sysUserMapper).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("重置密码")
    void testResetPassword() {
        when(passwordEncoder.encode("123456")).thenReturn("encoded_123456");
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        boolean result = sysUserService.resetPassword(1L, "123456");

        assertTrue(result);
        verify(passwordEncoder).encode("123456");
        verify(sysUserMapper).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("查询机构")
    void testGetOrgById() {
        when(sysOrgMapper.selectById(1L)).thenReturn(testOrg);

        SysOrg result = sysUserService.getOrgById(1L);

        assertNotNull(result);
        assertEquals("总部", result.getOrgName());
        verify(sysOrgMapper).selectById(1L);
    }

    @Test
    @DisplayName("查询角色")
    void testGetRoleById() {
        when(sysRoleMapper.selectById(1L)).thenReturn(testRole);

        SysRole result = sysUserService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRoleCode());
        verify(sysRoleMapper).selectById(1L);
    }
}
