package com.foodly.foodlyapi.controller;

import com.foodly.foodlyapi.model.OrderModel;
import com.foodly.foodlyapi.model.TaskDto;
import com.foodly.foodlyapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.task.api.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Sipariş İşlemleri", description = "BPMN tabanlı sipariş yönetimi ve workflow endpoint'leri")
public class OrderController {
    private final OrderService orderService;

    @Operation(
        summary = "1. ADIM: Yeni Sipariş Oluştur",
        description = """
            **Sipariş oluşturur ve BPMN sürecini başlatır**
            
            Request Body Örneği:
            ```json
            {
              "userId": 1,
              "restaurantId": 1
            }
            ```
            
            **Ne Olur:**
            - Sipariş PENDING durumunda kaydedilir
            - BPMN süreci otomatik başlar
            - PaymentApprovalDelegate çalışır (%70 başarı şansı)
            - Ödeme APPROVED veya REJECTED olur
            
            **Sonuç:** processInstanceId döner (BPMN takibi için)
            """
    )
    @PostMapping
    public ResponseEntity<OrderModel> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Sipariş bilgileri (userId ve restaurantId zorunlu)"
            )
            @Valid @RequestBody OrderModel orderModel) {
        log.info("Yeni sipariş talebi alındı - User ID: {}, Restaurant ID: {}", 
                orderModel.getUserId(), orderModel.getRestaurantId());
        
        OrderModel createdOrder = orderService.createOrder(orderModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(
        summary = "2. ADIM: Bekleyen Görevleri Listele",
        description = """
            **Belirli bir kullanıcıya atanmış bekleyen BPMN task'larını getirir**
            
            **Kullanıcı Rolleri:**
            - `restaurant` → Sipariş onaylama (reviewOrder)
            - `kitchen` → Sipariş hazırlama (prepareOrder)
            - `courier` → Teslimat (shipOrder)
            
            **Örnek Kullanım:**
            ```
            GET /api/orders/tasks/restaurant
            ```
            
            **Sonuç:** Task listesi (id, name, assignee bilgileriyle)
            
            **Not:** Ödeme onaylanmış siparişler için restoran'ın task'ı görünür
            """
    )
    @GetMapping("/tasks/{assignee}")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(
            @Parameter(description = "Kullanıcı rolü (restaurant, kitchen, courier)", example = "restaurant")
            @PathVariable String assignee) {
        log.info("Task listesi sorgulanıyor - Assignee: {}", assignee);
        List<TaskDto> tasks = orderService.getTasksByAssignee(assignee);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
        summary = "3. ADIM: Görevi Tamamla",
        description = """
            **UserTask'ı tamamlar ve BPMN sürecini ilerletir**
            
            **Task Türlerine Göre Request Body:**
            
            **Restoran Onayı (reviewOrder):**
            ```json
            {
              "approved": true
            }
            ```
            veya
            ```json
            {
              "approved": false
            }
            ```
            
            **Mutfak Hazırlama (prepareOrder):**
            ```json
            {}
            ```
            
            **Kurye Teslim (shipOrder):**
            ```json
            {}
            ```
            
            **Sonuç:** Task tamamlanır, süreç bir sonraki adıma geçer
            """
    )
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Void> completeTask(
            @Parameter(description = "Tamamlanacak task'ın ID'si", example = "12345")
            @PathVariable String taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Task değişkenleri (reviewOrder için approved:true/false, diğerleri için boş {})"
            )
            @RequestBody Map<String, Object> variables) {
        log.info("Task tamamlanıyor - Task ID: {}, Variables: {}", taskId, variables);
        orderService.completeTask(taskId, variables);
        return ResponseEntity.ok().build();
    }
}
