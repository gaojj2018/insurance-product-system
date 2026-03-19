package com.insurance.message.scheduler;

import com.insurance.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final MessageService messageService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void sendRenewalNotifications() {
        log.info("续期提醒已整合到 policy-service 的 RenewalScheduler 中统一处理");
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void sendPremiumDueNotifications() {
        log.info("保费到期提醒已整合到 policy-service 的 RenewalScheduler 中统一处理");
    }
}
