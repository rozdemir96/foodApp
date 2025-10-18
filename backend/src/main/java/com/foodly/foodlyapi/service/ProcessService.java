package com.foodly.foodlyapi.service;

import com.foodly.foodlyapi.model.ProcessInstanceDto;
import com.foodly.foodlyapi.model.TaskHistoryDto;

import java.util.List;
import java.util.Map;

public interface ProcessService {
    
    /**
     * Aktif (devam eden) tüm süreçleri getirir
     */
    List<ProcessInstanceDto> getActiveProcesses();
    
    /**
     * Tamamlanmış tüm süreçleri getirir
     */
    List<ProcessInstanceDto> getCompletedProcesses();
    
    /**
     * Belirli bir sipariş ID'sine göre süreç detayını getirir
     */
    Map<String, Object> getProcessDetailsByOrderId(Long orderId);
    
    /**
     * Belirli bir process instance ID'sine göre süreç geçmişini getirir
     */
    List<TaskHistoryDto> getProcessHistory(String processInstanceId);
}
