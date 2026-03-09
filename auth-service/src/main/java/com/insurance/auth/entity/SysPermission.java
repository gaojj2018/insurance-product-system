package com.insurance.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class SysPermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String permissionCode;
    private String permissionName;
    private String permissionType;
    private Long parentId;
    private String resourceUrl;
    private Integer sortOrder;
    
    @TableField(exist = false)
    private List<SysPermission> children;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
