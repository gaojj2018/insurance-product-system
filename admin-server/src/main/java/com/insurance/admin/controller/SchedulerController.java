package com.insurance.admin.controller;

import com.insurance.admin.scheduler.DynamicScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 任务调度Controller - 提供定时任务管理和执行的RESTful API
 */
@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {
    
    @Autowired
    private DynamicScheduler dynamicScheduler;
    
    private static final Map<Long, Map<String, Object>> taskConfigs = new HashMap<>();
    private static final Map<Long, String> taskKeys = new HashMap<>();
    
    static {
        taskConfigs.put(1L, Map.of(
            "id", 1L,
            "taskName", "佣金计算",
            "service", "finance-service",
            "cronExpression", "0 0 1 * * ?",
            "status", "RUNNING",
            "taskKey", "commissionCalculate"
        ));
        taskKeys.put(1L, "commissionCalculate");
        
        taskConfigs.put(2L, Map.of(
            "id", 2L,
            "taskName", "续期处理",
            "service", "policy-service",
            "cronExpression", "0 0 2 * * ?",
            "status", "RUNNING",
            "taskKey", "renewal"
        ));
        taskKeys.put(2L, "renewal");
        
        taskConfigs.put(3L, Map.of(
            "id", 3L,
            "taskName", "理赔结算",
            "service", "claims-service",
            "cronExpression", "0 0 3 * * ?",
            "status", "RUNNING",
            "taskKey", "claimsSettlement"
        ));
        taskKeys.put(3L, "claimsSettlement");
        
        taskConfigs.put(4L, Map.of(
            "id", 4L,
            "taskName", "小额理赔审核",
            "service", "claims-service",
            "cronExpression", "0 0 4 * * ?",
            "status", "RUNNING",
            "taskKey", "claimsAutoApprove"
        ));
        taskKeys.put(4L, "claimsAutoApprove");
    }
    
    @GetMapping("/tasks")
    public Map<String, Object> getTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();
        for (Map.Entry<Long, Map<String, Object>> entry : taskConfigs.entrySet()) {
            Map<String, Object> task = new HashMap<>(entry.getValue());
            task.put("nextRunTime", calculateNextRunTime((String) task.get("cronExpression")));
            tasks.add(task);
        }
        return Map.of("code", 200, "data", tasks);
    }
    
    @PostMapping("/task/update")
    public Map<String, Object> updateTask(@RequestBody Map<String, Object> request) {
        Long id = Long.valueOf(request.get("id").toString());
        String cronExpression = request.get("cronExpression").toString();
        
        if (taskConfigs.containsKey(id)) {
            Map<String, Object> task = new HashMap<>(taskConfigs.get(id));
            task.put("cronExpression", cronExpression);
            taskConfigs.put(id, task);
            
            String taskKey = taskKeys.get(id);
            if (taskKey != null) {
                dynamicScheduler.updateCron(taskKey, cronExpression);
            }
        }
        
        return Map.of("code", 200, "message", "更新成功");
    }
    
    @PostMapping("/task/pause/{id}")
    public Map<String, Object> pauseTask(@PathVariable Long id) {
        if (taskConfigs.containsKey(id)) {
            Map<String, Object> task = new HashMap<>(taskConfigs.get(id));
            task.put("status", "PAUSED");
            taskConfigs.put(id, task);
        }
        return Map.of("code", 200, "message", "已暂停");
    }
    
    @PostMapping("/task/resume/{id}")
    public Map<String, Object> resumeTask(@PathVariable Long id) {
        if (taskConfigs.containsKey(id)) {
            Map<String, Object> task = new HashMap<>(taskConfigs.get(id));
            task.put("status", "RUNNING");
            taskConfigs.put(id, task);
        }
        return Map.of("code", 200, "message", "已启动");
    }
    
    /**
     * 获取任务执行日志
     */
    @GetMapping("/logs")
    public Map<String, Object> getLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 生成最近7天的模拟执行记录
        String[] taskNames = {"佣金计算", "续期处理", "理赔结算", "小额理赔审核"};
        String[] statuses = {"成功", "成功", "成功", "成功"};
        int[] hours = {1, 2, 3, 4};
        
        for (int day = 1; day <= 7; day++) {
            cal.set(java.util.Calendar.DAY_OF_MONTH, cal.get(java.util.Calendar.DAY_OF_MONTH) - 1);
            
            for (int i = 0; i < taskNames.length; i++) {
                Map<String, Object> log = new HashMap<>();
                log.put("id", day * 10 + i);
                log.put("taskName", taskNames[i]);
                cal.set(java.util.Calendar.HOUR_OF_DAY, hours[i]);
                cal.set(java.util.Calendar.MINUTE, new java.util.Random().nextInt(60));
                cal.set(java.util.Calendar.SECOND, new java.util.Random().nextInt(60));
                log.put("executeTime", sdf.format(cal.getTime()));
                log.put("status", day == 1 ? "成功" : statuses[i]);
                
                String[] results = {
                    String.format("共计算 %d 笔佣金", new java.util.Random().nextInt(20) + 5),
                    String.format("待续期保单数: %d", new java.util.Random().nextInt(15) + 1),
                    String.format("已结算 %d 单", new java.util.Random().nextInt(10)),
                    String.format("自动通过 %d 单", new java.util.Random().nextInt(5))
                };
                log.put("result", results[i]);
                logs.add(log);
            }
        }
        return Map.of("code", 200, "data", logs);
    }
    
    private String getResultByTaskName(String taskName) {
        return switch (taskName) {
            case "佣金计算" -> "共计算 15 笔佣金";
            case "续期处理" -> "待续期保单数: 8";
            case "理赔结算" -> "已结算 3 单";
            case "小额理赔审核" -> "自动通过 2 单";
            default -> "执行完成";
        };
    }
    
    private String calculateNextRunTime(String cron) {
        try {
            String[] parts = cron.split(" ");
            if (parts.length >= 6) {
                int hour = Integer.parseInt(parts[2]);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                cal.set(java.util.Calendar.MINUTE, 0);
                cal.set(java.util.Calendar.SECOND, 0);
                if (cal.getTime().before(new Date())) {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            }
        } catch (Exception e) {
        }
        return "未知";
    }
}
