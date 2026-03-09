package com.insurance.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_org")
public class SysOrg {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orgCode;
    private String orgName;
    private Long parentId;
    private String parentPath;
    private String level;
    private Integer sortOrder;
    private String leader;
    private String phone;
    private String address;
    private String status;
    
    @TableField(exist = false)
    private List<SysOrg> children;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
