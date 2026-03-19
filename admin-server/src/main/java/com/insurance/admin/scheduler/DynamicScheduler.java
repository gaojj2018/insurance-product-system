package com.insurance.admin.scheduler;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicScheduler implements SchedulingConfigurer {
    
    private final Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, String> cronExpressions = new ConcurrentHashMap<>();
    private ScheduledTaskRegistrar taskRegistrar;
    
    public DynamicScheduler() {
        cronExpressions.put("commissionCalculate", "0 0 1 * * ?");
        cronExpressions.put("renewal", "0 0 2 * * ?");
        cronExpressions.put("claimsSettlement", "0 0 3 * * ?");
        cronExpressions.put("claimsAutoApprove", "0 0 4 * * ?");
        cronExpressions.put("notification", "0 0 6 * * ?");
    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }
    
    public void updateCron(String taskName, String cronExpression) {
        cronExpressions.put(taskName, cronExpression);
        
        if (tasks.containsKey(taskName)) {
            tasks.get(taskName).cancel();
        }
    }
    
    public String getCron(String taskName) {
        return cronExpressions.get(taskName);
    }
    
    public Map<String, String> getAllCrons() {
        return new HashMap<>(cronExpressions);
    }
    
    public static class ScheduledTask {
        private volatile boolean cancelled = false;
        
        public void cancel() {
            this.cancelled = true;
        }
        
        public boolean isCancelled() {
            return cancelled;
        }
    }
}
