package com.insurance.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String customerNo;
    
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 100, message = "客户姓名不能超过100个字符")
    private String customerName;
    
    @NotBlank(message = "证件类型不能为空")
    private String idType;
    
    @NotBlank(message = "证件号码不能为空")
    @Size(max = 50, message = "证件号码不能超过50个字符")
    private String idNo;
    
    private String gender;
    
    private LocalDateTime birthDate;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 500, message = "地址不能超过500个字符")
    private String address;
    
    @NotBlank(message = "客户类型不能为空")
    private String customerType;
    
    private String riskLevel;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
