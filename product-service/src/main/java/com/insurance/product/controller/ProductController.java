package com.insurance.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.product.dto.ProductQueryDTO;
import com.insurance.product.entity.Product;
import com.insurance.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品Controller
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 分页查询产品
     */
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> queryPage(@RequestBody ProductQueryDTO queryDTO) {
        IPage<Product> page = productService.queryPage(queryDTO);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", page);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据类型查询产品列表
     */
    @GetMapping("/type/{productType}")
    public ResponseEntity<Map<String, Object>> listByType(@PathVariable String productType) {
        List<Product> list = productService.listByType(productType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取产品详情
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long productId) {
        Product product = productService.getById(productId);
        
        Map<String, Object> result = new HashMap<>();
        if (product != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", product);
        } else {
            result.put("code", 404);
            result.put("message", "产品不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建产品
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Product product) {
        boolean success = productService.createProduct(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "创建成功" : "创建失败");
        result.put("data", product);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新产品
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Product product) {
        boolean success = productService.updateById(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "更新成功" : "更新失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除产品
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long productId) {
        boolean success = productService.removeById(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 发布产品
     */
    @PostMapping("/publish/{productId}")
    public ResponseEntity<Map<String, Object>> publish(@PathVariable Long productId) {
        boolean success = productService.publishProduct(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "发布成功" : "发布失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 停售产品
     */
    @PostMapping("/stop/{productId}")
    public ResponseEntity<Map<String, Object>> stop(@PathVariable Long productId) {
        boolean success = productService.stopProduct(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "停售成功" : "停售失败");
        
        return ResponseEntity.ok(result);
    }
}
