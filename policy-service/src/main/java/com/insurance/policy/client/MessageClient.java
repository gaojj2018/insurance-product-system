package com.insurance.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "message-service", path = "/api/message")
public interface MessageClient {
    
    @PostMapping("/send/renewal")
    void sendRenewalNotification(
            @RequestParam String mobile,
            @RequestParam String policyNo,
            @RequestParam String expiryDate);
    
    @PostMapping("/send/premium-due")
    void sendPremiumDueNotification(
            @RequestParam String mobile,
            @RequestParam String policyNo,
            @RequestParam String amount,
            @RequestParam String dueDate);
}
