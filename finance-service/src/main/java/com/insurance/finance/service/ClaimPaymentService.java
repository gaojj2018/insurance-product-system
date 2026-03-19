package com.insurance.finance.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.finance.entity.ClaimPayment;
import com.insurance.finance.repository.ClaimPaymentMapper;
import org.springframework.stereotype.Service;

/**
 * 理赔款Service - 提供理赔款业务逻辑处理
 */
@Service
public class ClaimPaymentService extends ServiceImpl<ClaimPaymentMapper, ClaimPayment> {
}
