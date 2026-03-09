package com.insurance.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.product.entity.Coverage;
import com.insurance.product.service.CoverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 险种Controller
 */
@RestController
@RequestMapping("/api/coverage")
public class CoverageController {
    
    @Autowired
    private CoverageService coverageService;
    
    /**
     * 分页查询险种
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long productId) {
        
        IPage<Coverage> page = coverageService.queryPage(pageNum, pageSize, productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", page);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据产品ID查询险种列表
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> listByProductId(@PathVariable Long productId) {
        List<Coverage> list = coverageService.listByProductId(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取险种详情
     */
    @GetMapping("/{coverageId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long coverageId) {
        Coverage coverage = coverageService.getById(coverageId);
        
        Map<String, Object> result = new HashMap<>();
        if (coverage != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", coverage);
        } else {
            result.put("code", 404);
            result.put("message", "险种不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建险种
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Coverage coverage) {
        boolean success = coverageService.createCoverage(coverage);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "创建成功" : "创建失败");
        result.put("data", coverage);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新险种
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Coverage coverage) {
        boolean success = coverageService.updateById(coverage);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "更新成功" : "更新失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除险种
     */
    @DeleteMapping("/{coverageId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long coverageId) {
        boolean success = coverageService.removeById(coverageId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        
        return ResponseEntity.ok(result);
    }
}
