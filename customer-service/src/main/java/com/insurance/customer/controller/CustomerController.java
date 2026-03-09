package com.insurance.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insurance.customer.entity.Customer;
import com.insurance.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody Customer customer) {
        Customer created = customerService.createCustomer(customer);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", created);
        result.put("message", "创建成功");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        Map<String, Object> result = new HashMap<>();
        if (customer != null) {
            result.put("code", 200);
            result.put("data", customer);
            result.put("message", "查询成功");
        } else {
            result.put("code", 404);
            result.put("message", "客户不存在");
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/no/{customerNo}")
    public ResponseEntity<Map<String, Object>> getByCustomerNo(@PathVariable String customerNo) {
        Customer customer = customerService.findByCustomerNo(customerNo);
        Map<String, Object> result = new HashMap<>();
        if (customer != null) {
            result.put("code", 200);
            result.put("data", customer);
            result.put("message", "查询成功");
        } else {
            result.put("code", 404);
            result.put("message", "客户不存在");
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Customer> page = customerService.findPage(pageNum, pageSize, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", page);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCount() {
        long count = customerService.countCustomers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of("count", count));
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        boolean success = customerService.updateCustomer(customer);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "更新成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新失败");
        }
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = customerService.deleteCustomer(id);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除失败");
        }
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/freeze/{id}")
    public ResponseEntity<Map<String, Object>> freeze(@PathVariable Long id) {
        boolean success = customerService.freezeCustomer(id);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "冻结成功");
        } else {
            result.put("code", 400);
            result.put("message", "冻结失败");
        }
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/unfreeze/{id}")
    public ResponseEntity<Map<String, Object>> unfreeze(@PathVariable Long id) {
        boolean success = customerService.unfreezeCustomer(id);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "解冻成功");
        } else {
            result.put("code", 400);
            result.put("message", "解冻失败");
        }
        return ResponseEntity.ok(result);
    }
}
