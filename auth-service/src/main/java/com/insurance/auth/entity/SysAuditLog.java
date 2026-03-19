package com.insurance.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_audit_log")
public class SysAuditLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String operation;
    
    private String module;
    
    private String method;
    
    private String requestUrl;
    
    private String requestMethod;
    
    private String requestParams;
    
    private String responseResult;
    
    private Integer responseStatus;
    
    private String ip;
    
    private String userAgent;
    
    private Long executionTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
