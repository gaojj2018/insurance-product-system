package com.insurance.message.controller;

import com.insurance.message.entity.MessageRecord;
import com.insurance.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping("/sms")
    public ResponseEntity<Map<String, Object>> sendSms(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String content = request.get("content");
        
        MessageRecord record = messageService.sendSms(mobile, content);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", record);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String subject = request.get("subject");
        String content = request.get("content");
        
        MessageRecord record = messageService.sendEmail(email, subject, content);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", record);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<Map<String, Object>> findByRecipient(@PathVariable String recipient) {
        List<MessageRecord> records = messageService.findByRecipient(recipient);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
}
