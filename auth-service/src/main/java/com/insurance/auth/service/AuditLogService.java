package com.insurance.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.auth.entity.SysAuditLog;
import com.insurance.auth.repository.SysAuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final SysAuditLogMapper auditLogMapper;
    
    public void log(String username, String operation, String module, String method,
                    String requestUrl, String requestMethod, String requestParams,
                    String responseResult, Integer responseStatus, String ip,
                    String userAgent, Long executionTime) {
        SysAuditLog log = new SysAuditLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setModule(module);
        log.setMethod(method);
        log.setRequestUrl(requestUrl);
        log.setRequestMethod(requestMethod);
        log.setRequestParams(requestParams);
        log.setResponseResult(responseResult);
        log.setResponseStatus(responseStatus);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setExecutionTime(executionTime);
        log.setCreateTime(LocalDateTime.now());
        
        auditLogMapper.insert(log);
    }
    
    public IPage<SysAuditLog> getPage(int pageNum, int pageSize, String username, 
                                       String module, String operation) {
        Page<SysAuditLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAuditLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysAuditLog::getUsername, username);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq(SysAuditLog::getModule, module);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like(SysAuditLog::getOperation, operation);
        }
        
        wrapper.orderByDesc(SysAuditLog::getCreateTime);
        return auditLogMapper.selectPage(page, wrapper);
    }
}
