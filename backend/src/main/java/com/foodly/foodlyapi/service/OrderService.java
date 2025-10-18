package com.foodly.foodlyapi.service;

import com.foodly.foodlyapi.model.OrderModel;
import com.foodly.foodlyapi.model.TaskDto;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderModel createOrder(OrderModel orderModel);
    
    /**
     * Belirli bir kullanıcıya atanmış aktif task'ları getirir
     */
    List<TaskDto> getTasksByAssignee(String assignee);
    
    /**
     * Task'ı tamamlar ve sürecin devam etmesini sağlar
     */
    void completeTask(String taskId, Map<String, Object> variables);
}