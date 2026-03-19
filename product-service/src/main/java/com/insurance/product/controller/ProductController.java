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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 产品管理Controller
 * 提供产品相关的RESTful API接口
 * 
 * API列表:
 * - POST /api/product/count - 获取产品总数
 * - POST /api/product/page - 分页查询产品
 * - GET /api/product/type/{productType} - 根据类型查询产品
 * - GET /api/product/{productId} - 获取产品详情
 * - POST /api/product - 创建产品
 * - PUT /api/product - 更新产品
 * - DELETE /api/product/{productId} - 删除产品
 * - POST /api/product/publish/{productId} - 发布产品
 * - POST /api/product/stop/{productId} - 停售产品
 * - GET /api/product/compare - 产品对比
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("/count")
    public ResponseEntity<Map<String, Object>> count() {
        long count = productService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of("count", count));
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }
    
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
        var references = productService.checkBusinessReferences(productId);
        
        Map<String, Object> result = new HashMap<>();
        if (!references.isEmpty()) {
            result.put("code", 400);
            result.put("message", "该产品有关联的业务记录，无法删除");
            result.put("data", references);
            return ResponseEntity.ok(result);
        }
        
        boolean success = productService.removeById(productId);
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
    
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compare(@RequestParam String ids) {
        String[] idArr = ids.split(",");
        List<Long> idList = Arrays.stream(idArr).map(Long::parseLong).collect(Collectors.toList());
        
        List<Product> products = productService.listByIds(idList);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of(
            "products", products,
            "comparison", buildComparison(products)
        ));
        
        return ResponseEntity.ok(result);
    }
    
    private List<Map<String, Object>> buildComparison(List<Product> products) {
        List<Map<String, Object>> comparison = new ArrayList<>();
        Map<String, Object> values;
        
        // 产品代码
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), p.getProductCode());
        }
        comparison.add(Map.of("feature", "产品代码", "values", values));
        
        // 产品类型
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), getTypeName(p.getProductType()));
        }
        comparison.add(Map.of("feature", "产品类型", "values", values));
        
        // 保障期间
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), p.getCoveragePeriod() != null ? p.getCoveragePeriod() : "-");
        }
        comparison.add(Map.of("feature", "保障期间", "values", values));
        
        // 缴费期间
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), p.getPaymentPeriod() != null ? p.getPaymentPeriod() : "-");
        }
        comparison.add(Map.of("feature", "缴费期间", "values", values));
        
        // 最低保额
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), p.getMinCoverage() != null ? p.getMinCoverage() + "元" : "-");
        }
        comparison.add(Map.of("feature", "最低保额", "values", values));
        
        // 最高保额
        values = new HashMap<>();
        for (Product p : products) {
            values.put(p.getProductId().toString(), p.getMaxCoverage() != null ? p.getMaxCoverage() + "元" : "-");
        }
        comparison.add(Map.of("feature", "最高保额", "values", values));
        
        return comparison;
    }
    
    private String getTypeName(String type) {
        return switch (type) {
            case "LIFE" -> "人寿保险";
            case "PROPERTY" -> "财产保险";
            case "ACCIDENT" -> "意外保险";
            case "HEALTH" -> "健康保险";
            default -> type;
        };
    }
}
