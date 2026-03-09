package com.insurance.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.customer.entity.Customer;
import com.insurance.customer.repository.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {
    
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
}
