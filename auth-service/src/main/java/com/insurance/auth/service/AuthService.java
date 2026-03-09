package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.auth.config.JwtTokenProvider;
import com.insurance.auth.entity.SysUser;
import com.insurance.auth.repository.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthService extends ServiceImpl<SysUserMapper, SysUser> {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SysUser user = this.getOne(new QueryWrapper<SysUser>()
                    .eq("username", username)
                    .eq("status", "ACTIVE"));
            
            if (user == null) {
                log.warn("登录失败: 用户不存在, username={}", username);
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            
            if (!passwordMatch) {
                log.warn("登录失败: 密码错误, username={}", username);
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            List<String> permissions = getDefaultPermissions(user.getRole());
            
            String token = jwtTokenProvider.generateToken(username, user.getId(), user.getRole(), permissions);
            
            log.info("登录成功: username={}, role={}", username, user.getRole());
            
            result.put("success", true);
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("role", user.getRole());
            result.put("permissions", permissions);
            
        } catch (Exception e) {
            log.error("登录异常: username={}, error={}", username, e.getMessage(), e);
            result.put("success", false);
            result.put("message", "登录失败，请稍后重试");
        }
        
        return result;
    }
    
    private List<String> getDefaultPermissions(String role) {
        List<String> perms = new ArrayList<>();
        if ("ADMIN".equals(role)) {
            perms.add("system");
            perms.add("system:org");
            perms.add("system:role");
            perms.add("system:user");
            perms.add("product");
            perms.add("product:create");
            perms.add("product:edit");
            perms.add("product:delete");
            perms.add("product:publish");
            perms.add("application");
            perms.add("application:create");
            perms.add("application:view");
            perms.add("underwriting");
            perms.add("underwriting:view");
            perms.add("underwriting:approve");
            perms.add("underwriting:reject");
            perms.add("policy");
            perms.add("policy:view");
            perms.add("claims");
            perms.add("claims:view");
            perms.add("claims:create");
            perms.add("claims:process");
            perms.add("claims:pay");
            perms.add("customer");
            perms.add("customer:view");
            perms.add("customer:create");
            perms.add("customer:edit");
            perms.add("customer:delete");
            perms.add("finance");
            perms.add("finance:view");
        } else if ("AGENT".equals(role)) {
            perms.add("product:view");
            perms.add("application:view");
            perms.add("application:create");
            perms.add("customer:view");
            perms.add("customer:create");
        } else if ("UNDERWRITER".equals(role)) {
            perms.add("product:view");
            perms.add("application:view");
            perms.add("underwriting:view");
            perms.add("underwriting:approve");
            perms.add("underwriting:reject");
        } else if ("CLAIMS_OFFICER".equals(role)) {
            perms.add("claims:view");
            perms.add("claims:create");
            perms.add("claims:process");
            perms.add("claims:pay");
        } else {
            perms.add("product:view");
            perms.add("application:view");
        }
        return perms;
    }
    
    public SysUser register(SysUser user) {
        SysUser existing = this.getOne(new QueryWrapper<SysUser>()
                .eq("username", user.getUsername()));
        
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("ACTIVE");
        this.save(user);
        
        log.info("用户注册成功: username={}", user.getUsername());
        
        return user;
    }
    
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
