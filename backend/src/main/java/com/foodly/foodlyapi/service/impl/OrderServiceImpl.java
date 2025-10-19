package com.foodly.foodlyapi.service.impl;

import com.foodly.foodlyapi.entity.Order;
import com.foodly.foodlyapi.enums.OrderStatus;
import com.foodly.foodlyapi.mapper.OrderMapper;
import com.foodly.foodlyapi.model.OrderModel;
import com.foodly.foodlyapi.model.TaskDto;
import com.foodly.foodlyapi.repository.OrderRepository;
import com.foodly.foodlyapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    
    /**
     * Flowable RuntimeService - BPMN süreçlerini başlatmak ve yönetmek için kullanılır
     */
    private final RuntimeService runtimeService;
    
    /**
     * Flowable TaskService - UserTask'ları sorgulamak ve tamamlamak için kullanılır
     */
    private final TaskService taskService;

    @Override
    @Transactional
    public OrderModel createOrder(OrderModel orderModel) {
        // 1. Siparişi PENDING durumunda oluştur
        orderModel.setStatus(OrderStatus.PENDING);
        orderModel.setOrderDate(LocalDateTime.now());
        
        Order order = orderMapper.mapToEntity(orderModel);
        Order savedOrder = orderRepository.save(order);
        
        log.info("Sipariş oluşturuldu - Order ID: {}", savedOrder.getId());
        
        // 2. BPMN sürecini başlat (foodOrderProcess)
        // Bu adım BPMN'deki "start" eventi tetikler
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("orderId", savedOrder.getId());
        processVariables.put("userId", savedOrder.getUserId());
        processVariables.put("restaurantId", savedOrder.getRestaurantId());
        
        /**
         * RuntimeService.startProcessInstanceByKey() metodu ile BPMN süreci başlatılır
         * - "foodOrderProcess": BPMN dosyasındaki process id
         * - processVariables: Süreç boyunca kullanılacak değişkenler
         * Bu çağrı ile BPMN'deki "start" eventi tetiklenir ve süreç akışı başlar
         */
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "foodOrderProcess", 
                processVariables
        );
        
        // 3. Process Instance ID'yi siparişe kaydet (süreç takibi için)
        savedOrder.setProcessInstanceId(processInstance.getId());
        savedOrder = orderRepository.save(savedOrder);
        
        log.info("BPMN süreci başlatıldı - Process Instance ID: {}, Order ID: {}", 
                processInstance.getId(), savedOrder.getId());
        
        return orderMapper.mapToModel(savedOrder);
    }

    @Override
    public List<TaskDto> getTasksByAssignee(String assignee) {
        /**
         * TaskService.createTaskQuery() ile belirli bir kullanıcıya atanmış
         * aktif UserTask'ları sorgulayabiliriz
         */
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        
        return tasks.stream()
                .map(this::convertTaskToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Task entity'sini DTO'ya dönüştürür (JSON serialization için)
     */
    private TaskDto convertTaskToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .assignee(task.getAssignee())
                .createTime(task.getCreateTime())
                .processInstanceId(task.getProcessInstanceId())
                .build();
    }

    @Override
    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables) {
        /**
         * TaskService.complete() metodu ile UserTask tamamlanır
         * - taskId: Tamamlanacak task'ın ID'si
         * - variables: Task tamamlanırken BPMN sürecine aktarılacak değişkenler
         *   (Örn: approved=true/false gibi gateway kararları için)
         * 
         * Task tamamlandığında BPMN süreci bir sonraki adıma ilerler
         */
        
        // Restoran reddetmesi kontrolü - approved=false ise order'ı REJECTED yap
        if (variables.containsKey("approved") && Boolean.FALSE.equals(variables.get("approved"))) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task != null) {
                Long orderId = (Long) runtimeService.getVariable(task.getProcessInstanceId(), "orderId");
                if (orderId != null) {
                    orderRepository.findById(orderId).ifPresent(order -> {
                        order.setStatus(OrderStatus.REJECTED);
                        orderRepository.save(order);
                        log.info("Restoran siparişi reddetti - Order ID: {} REJECTED olarak işaretlendi", orderId);
                    });
                }
            }
        }
        
        taskService.complete(taskId, variables);
        log.info("Task tamamlandı - Task ID: {}", taskId);
    }
}
