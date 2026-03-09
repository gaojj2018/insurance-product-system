package com.insurance.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页请求基类
 * 用于接收分页查询参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否升序（默认降序）
     */
    private Boolean isAsc = false;

    /**
     * 获取MyBatis Plus的起始位置
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 校验并修正分页参数
     */
    public void validate() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大每页数量
        }
    }
}
