package com.insurance.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.product.entity.Clause;
import com.insurance.product.service.ClauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条款Controller
 */
@RestController
@RequestMapping("/api/clause")
public class ClauseController {
    
    @Autowired
    private ClauseService clauseService;
    
    /**
     * 分页查询条款
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long productId) {
        
        IPage<Clause> page = clauseService.queryPage(pageNum, pageSize, productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", page);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据产品ID查询条款列表
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> listByProductId(@PathVariable Long productId) {
        List<Clause> list = clauseService.listByProductId(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取条款详情
     */
    @GetMapping("/{clauseId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long clauseId) {
        Clause clause = clauseService.getById(clauseId);
        
        Map<String, Object> result = new HashMap<>();
        if (clause != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", clause);
        } else {
            result.put("code", 404);
            result.put("message", "条款不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建条款
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Clause clause) {
        boolean success = clauseService.createClause(clause);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "创建成功" : "创建失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新条款
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Clause clause) {
        boolean success = clauseService.updateById(clause);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "更新成功" : "更新失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除条款
     */
    @DeleteMapping("/{clauseId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long clauseId) {
        boolean success = clauseService.removeById(clauseId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        
        return ResponseEntity.ok(result);
    }
}
