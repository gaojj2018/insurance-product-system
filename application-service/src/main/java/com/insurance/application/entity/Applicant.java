package com.insurance.application.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("applicant")
public class Applicant {
    
    @TableId(type = IdType.AUTO)
    private Long applicantId;
    
    private String name;
    
    private String idType;
    
    private String idNo;
    
    private String gender;
    
    private LocalDateTime birthDate;
    
    private String nationality;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String address;
    
    private String mobile;
    
    private String email;
    
    private String occupation;
    
    private String occupationCategory;
    
    private String annualIncome;
    
    private String relationToInsured;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
