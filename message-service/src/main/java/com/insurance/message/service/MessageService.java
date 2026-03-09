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
}
