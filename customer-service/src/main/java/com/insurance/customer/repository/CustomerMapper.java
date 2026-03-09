package com.insurance.customer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insurance.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
