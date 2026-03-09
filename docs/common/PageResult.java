package com.insurance.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应对象
 * 
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long pageNum, Long pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
        this.list = list;
    }

    /**
     * 从MyBatis Plus的Page对象转换
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 快速创建
     */
    public static <T> PageResult<T> of(Long pageNum, Long pageSize, Long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }
}
