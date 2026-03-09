package com.insurance.common.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于Controller方法上，标注需要的权限
 * 
 * 使用示例：
 * <pre>
 * @RequirePermission("product:create")
 * @PostMapping
 * public Result<Product> create(@RequestBody Product product) { ... }
 * 
 * @RequirePermission(value = {"product:view", "product:edit"}, logical = Logical.OR)
 * @GetMapping("/{id}")
 * public Result<Product> getById(@PathVariable Long id) { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限码
     */
    String[] value();

    /**
     * 多个权限之间的逻辑关系
     */
    Logical logical() default Logical.AND;
}
