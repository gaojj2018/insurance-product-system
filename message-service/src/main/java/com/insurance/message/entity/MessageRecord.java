package com.insurance.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message_record")
public class MessageRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String messageType;
    private String recipient;
    private String subject;
    private String content;
    private String status;
    private LocalDateTime sendTime;
    private String errorMsg;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
