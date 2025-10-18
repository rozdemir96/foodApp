package com.foodly.foodlyapi.service.impl;

import com.foodly.foodlyapi.entity.Order;
import com.foodly.foodlyapi.model.ProcessInstanceDto;
import com.foodly.foodlyapi.model.TaskHistoryDto;
import com.foodly.foodlyapi.repository.OrderRepository;
import com.foodly.foodlyapi.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessServiceImpl implements ProcessService {

    /**
     * RuntimeService - Aktif BPMN süreçlerini sorgulamak için
     */
    private final RuntimeService runtimeService;
    
    /**
     * HistoryService - Tamamlanmış BPMN süreçlerini ve geçmişi sorgulamak için
     */
    private final HistoryService historyService;
    
    private final OrderRepository orderRepository;

    @Override
    public List<ProcessInstanceDto> getActiveProcesses() {
        /**
         * RuntimeService.createProcessInstanceQuery() ile aktif süreçleri sorgulayabiliriz
         * processDefinitionKey = "foodOrderProcess" ile sadece sipariş süreçlerini filtreleriz
         */
        List<ProcessInstance> processes = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("foodOrderProcess")
                .list();
        
        return processes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ProcessInstance'ı DTO'ya dönüştürür (JSON serialization için)
     */
    private ProcessInstanceDto convertToDto(ProcessInstance process) {
        Map<String, Object> variables = runtimeService.getVariables(process.getId());
        
        return ProcessInstanceDto.builder()
                .id(process.getId())
                .processDefinitionId(process.getProcessDefinitionId())
                .processDefinitionKey(process.getProcessDefinitionKey())
                .startTime(process.getStartTime())
                .status("ACTIVE")
                .variables(variables)
                .build();
    }

    @Override
    public List<ProcessInstanceDto> getCompletedProcesses() {
        /**
         * HistoryService.createHistoricProcessInstanceQuery() ile tamamlanmış süreçleri sorgulayabiliriz
         * finished() - Sadece bitmiş süreçleri getirir
         */
        List<HistoricProcessInstance> processes = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey("foodOrderProcess")
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .list();
        
        return processes.stream()
                .map(this::convertHistoricToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * HistoricProcessInstance'ı DTO'ya dönüştürür (JSON serialization için)
     */
    private ProcessInstanceDto convertHistoricToDto(HistoricProcessInstance process) {
        return ProcessInstanceDto.builder()
                .id(process.getId())
                .processDefinitionId(process.getProcessDefinitionId())
                .processDefinitionKey(process.getProcessDefinitionKey())
                .startTime(process.getStartTime())
                .endTime(process.getEndTime())
                .status("COMPLETED")
                .build();
    }

    @Override
    public Map<String, Object> getProcessDetailsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        String processInstanceId = order.getProcessInstanceId();
        
        if (processInstanceId == null) {
            throw new RuntimeException("Process not started for order: " + orderId);
        }
        
        Map<String, Object> details = new HashMap<>();
        details.put("orderId", orderId);
        details.put("orderStatus", order.getStatus());
        details.put("processInstanceId", processInstanceId);
        
        /**
         * Önce aktif süreçlerde ara
         */
        ProcessInstance activeProcess = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        
        if (activeProcess != null) {
            details.put("processStatus", "ACTIVE");
            details.put("processStartTime", activeProcess.getStartTime());
            
            /**
             * RuntimeService.getVariables() ile süreçteki tüm değişkenleri alabiliriz
             */
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            details.put("variables", variables);
        } else {
            /**
             * Aktif değilse, geçmişe bak (tamamlanmış veya iptal edilmiş)
             */
            HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            
            if (historicProcess != null) {
                details.put("processStatus", "COMPLETED");
                details.put("processStartTime", historicProcess.getStartTime());
                details.put("processEndTime", historicProcess.getEndTime());
                details.put("deleteReason", historicProcess.getDeleteReason());
            }
        }
        
        return details;
    }

    @Override
    public List<TaskHistoryDto> getProcessHistory(String processInstanceId) {
        /**
         * HistoryService.createHistoricTaskInstanceQuery() ile
         * bir süreçte tamamlanmış tüm task'ları sıralı olarak alabiliriz
         * Bu sayede sürecin hangi adımlardan geçtiğini görebiliriz
         */
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .list();
        
        return tasks.stream()
                .map(this::convertTaskToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * HistoricTaskInstance'ı DTO'ya dönüştürür (JSON serialization için)
     */
    private TaskHistoryDto convertTaskToDto(HistoricTaskInstance task) {
        return TaskHistoryDto.builder()
                .id(task.getId())
                .name(task.getName())
                .assignee(task.getAssignee())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .durationInMillis(task.getDurationInMillis())
                .build();
    }
}
