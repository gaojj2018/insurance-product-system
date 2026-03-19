package com.insurance.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insurance.message.entity.MessageRecord;
import com.insurance.message.repository.MessageRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService extends ServiceImpl<MessageRecordMapper, MessageRecord> {
    
    @Transactional
    public MessageRecord sendSms(String mobile, String content) {
        MessageRecord record = new MessageRecord();
        record.setMessageType("SMS");
        record.setRecipient(mobile);
        record.setContent(content);
        record.setStatus("PENDING");
        
        this.save(record);
        
        record.setStatus("SENT");
        record.setSendTime(LocalDateTime.now());
        this.updateById(record);
        
        return record;
    }
    
    @Transactional
    public MessageRecord sendEmail(String email, String subject, String content) {
        MessageRecord record = new MessageRecord();
        record.setMessageType("EMAIL");
        record.setRecipient(email);
        record.setSubject(subject);
        record.setContent(content);
        record.setStatus("PENDING");
        
        this.save(record);
        
        record.setStatus("SENT");
        record.setSendTime(LocalDateTime.now());
        this.updateById(record);
        
        return record;
    }
    
    public List<MessageRecord> findByRecipient(String recipient) {
        return this.list(new QueryWrapper<MessageRecord>()
                .eq("recipient", recipient)
                .orderByDesc("create_time"));
    }
    
    public List<MessageRecord> findByStatus(String status) {
        return this.list(new QueryWrapper<MessageRecord>()
                .eq("status", status)
                .orderByDesc("create_time"));
    }
    
    public void notifyPolicyCreated(String mobile, String policyNo, String productName) {
        String content = String.format("恭喜！您的保单%s（%s）已成功生效，请妥善保管。", policyNo, productName);
        sendSms(mobile, content);
    }
    
    public void notifyClaimSubmitted(String mobile, String claimNo) {
        String content = String.format("您提交的理赔申请%s已受理，我们将在3个工作日内完成审核，请留意查收通知。", claimNo);
        sendSms(mobile, content);
    }
    
    public void notifyClaimApproved(String mobile, String claimNo, String amount) {
        String content = String.format("您申请的理赔%s已审核通过，理赔金额%s元将于3个工作日内到账，请注意查收。", claimNo, amount);
        sendSms(mobile, content);
    }
    
    public void notifyPolicyRenewal(String mobile, String policyNo, String expiryDate) {
        String content = String.format("您的保单%s将于%s到期，请及时续费以保障您的权益。", policyNo, expiryDate);
        sendSms(mobile, content);
    }
    
    public void notifyPremiumDue(String mobile, String policyNo, String amount, String dueDate) {
        String content = String.format("您的保单%s保费%s元将于%s到期，请及时缴纳。", policyNo, amount, dueDate);
        sendSms(mobile, content);
    }
}
