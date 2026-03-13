package com.insurance.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.customer.client.ApplicationClient;
import com.insurance.customer.client.PolicyClient;
import com.insurance.customer.entity.Customer;
import com.insurance.customer.repository.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {
    
    private final ApplicationClient applicationClient;
    private final PolicyClient policyClient;
    
    @Transactional
    public Customer createCustomer(Customer customer) {
        customer.setCustomerNo("C" + System.currentTimeMillis());
        if (customer.getStatus() == null) {
            customer.setStatus("ACTIVE");
        }
        if (customer.getCustomerType() == null) {
            customer.setCustomerType("PERSONAL");
        }
        if (customer.getRiskLevel() == null) {
            customer.setRiskLevel("NORMAL");
        }
        this.save(customer);
        return customer;
    }
    
    public Customer findByCustomerNo(String customerNo) {
        return this.getOne(new QueryWrapper<Customer>()
                .eq("customer_no", customerNo));
    }
    
    public Customer findByIdNo(String idNo) {
        return this.getOne(new QueryWrapper<Customer>()
                .eq("id_no", idNo));
    }
    
    public Page<Customer> findPage(int pageNum, int pageSize, String keyword) {
        Page<Customer> page = new Page<>(pageNum, pageSize);
        
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(w -> w
                    .like("customer_name", keyword)
                    .or()
                    .like("mobile", keyword)
                    .or()
                    .like("customer_no", keyword));
        }
        
        return this.page(page, queryWrapper);
    }
    
    public List<Customer> findByMobile(String mobile) {
        return this.list(new QueryWrapper<Customer>()
                .eq("mobile", mobile));
    }
    
    @Transactional
    public boolean updateCustomer(Customer customer) {
        return this.updateById(customer);
    }
    
    @Transactional
    public boolean deleteCustomer(Long id) {
        return this.removeById(id);
    }
    
    @Transactional
    public boolean freezeCustomer(Long id) {
        Customer customer = this.getById(id);
        if (customer != null) {
            customer.setStatus("FROZEN");
            return this.updateById(customer);
        }
        return false;
    }
    
    @Transactional
    public boolean unfreezeCustomer(Long id) {
        Customer customer = this.getById(id);
        if (customer != null) {
            customer.setStatus("ACTIVE");
            return this.updateById(customer);
        }
        return false;
    }
    
    public long countCustomers() {
        return this.count();
    }
    
    public List<Map<String, Object>> checkBusinessReferences(Long customerId) {
        List<Map<String, Object>> references = new ArrayList<>();
        
        try {
            Map<String, Object> appResponse = applicationClient.getByCustomerId(customerId);
            if (appResponse != null && appResponse.get("code") != null 
                    && (Integer) appResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> applications = (List<Map<String, Object>>) appResponse.get("data");
                if (applications != null && !applications.isEmpty()) {
                    for (Map<String, Object> app : applications) {
                        references.add(Map.of(
                            "type", "APPLICATION",
                            "id", app.get("applicationId"),
                            "no", app.get("applicationNo"),
                            "status", app.get("status"),
                            "message", "投保单 " + app.get("applicationNo") + " (状态: " + app.get("status") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        try {
            Map<String, Object> policyResponse = policyClient.getByCustomerId(customerId);
            if (policyResponse != null && policyResponse.get("code") != null 
                    && (Integer) policyResponse.get("code") == 200) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> policies = (List<Map<String, Object>>) policyResponse.get("data");
                if (policies != null && !policies.isEmpty()) {
                    for (Map<String, Object> policy : policies) {
                        references.add(Map.of(
                            "type", "POLICY",
                            "id", policy.get("policyId"),
                            "no", policy.get("policyNo"),
                            "status", policy.get("status"),
                            "message", "保单 " + policy.get("policyNo") + " (状态: " + policy.get("status") + ")"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            // 服务不可用时跳过检查
        }
        
        return references;
    }
}
