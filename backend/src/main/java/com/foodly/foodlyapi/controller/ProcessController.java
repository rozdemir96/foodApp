package com.foodly.foodlyapi.controller;

import com.foodly.foodlyapi.model.ProcessInstanceDto;
import com.foodly.foodlyapi.model.TaskHistoryDto;
import com.foodly.foodlyapi.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
@Tag(name = "BPMN Süreç Sorgulama", description = "Aktif ve tamamlanmış BPMN süreçlerini sorgulama endpoint'leri")
public class ProcessController {

    private final ProcessService processService;

    @Operation(
        summary = "Aktif Süreçleri Listele",
        description = """
            **Şu anda devam eden (aktif) tüm BPMN süreçlerini getirir**
            
            **Ne zaman aktif olur:**
            - Ödeme onaylanmış ama restoran henüz onaylamamış
            - Restoran onaylamış ama mutfak hazırlamamış
            - Mutfak hazırlamış ama kurye teslim etmemiş
            
            **Örnek Kullanım:**
            ```
            GET /api/processes/active
            ```
            
            **Sonuç:** Process Instance ID, başlangıç zamanı, değişkenler vb.
            """
    )
    @GetMapping("/active")
    public ResponseEntity<List<ProcessInstanceDto>> getActiveProcesses() {
        log.info("Aktif süreçler sorgulanıyor");
        List<ProcessInstanceDto> processes = processService.getActiveProcesses();
        return ResponseEntity.ok(processes);
    }

    @Operation(
        summary = "Tamamlanmış Süreçleri Listele",
        description = """
            **Tamamlanmış (bitmiş) tüm BPMN süreçlerini getirir**
            
            **Ne zaman tamamlanır:**
            - Sipariş başarıyla teslim edildi (DELIVERED)
            - Ödeme reddedildi (REJECTED)
            - Restoran siparişi reddetti (REJECTED)
            
            **Örnek Kullanım:**
            ```
            GET /api/processes/completed
            ```
            
            **Sonuç:** Başlangıç/bitiş zamanı, süre, sonuç vb.
            """
    )
    @GetMapping("/completed")
    public ResponseEntity<List<ProcessInstanceDto>> getCompletedProcesses() {
        log.info("Tamamlanmış süreçler sorgulanıyor");
        List<ProcessInstanceDto> processes = processService.getCompletedProcesses();
        return ResponseEntity.ok(processes);
    }

    @Operation(
        summary = "Sipariş ID'sine Göre Süreç Detayı",
        description = """
            **Belirli bir siparişin BPMN süreç detaylarını getirir**
            
            **Dönen Bilgiler:**
            - Sipariş ID ve durumu
            - Process Instance ID
            - Süreç durumu (ACTIVE/COMPLETED)
            - Başlangıç/bitiş zamanı
            - Süreçteki değişkenler (orderId, userId, approved vb.)
            
            **Örnek Kullanım:**
            ```
            GET /api/processes/order/1
            ```
            """
    )
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getProcessDetailsByOrderId(
            @Parameter(description = "Sipariş ID'si", example = "1")
            @PathVariable Long orderId) {
        log.info("Sipariş süreci sorgulanıyor - Order ID: {}", orderId);
        Map<String, Object> details = processService.getProcessDetailsByOrderId(orderId);
        return ResponseEntity.ok(details);
    }

    @Operation(
        summary = "Süreç Geçmişini Görüntüle",
        description = """
            **Bir BPMN sürecinin hangi adımlardan geçtiğini kronolojik sırayla gösterir**
            
            **Her adım için:**
            - Task adı (reviewOrder, prepareOrder, shipOrder)
            - Başlangıç/bitiş zamanı
            - Kim tamamladı (assignee)
            - Ne kadar sürdü
            
            **Örnek Kullanım:**
            ```
            GET /api/processes/44bf4bea-ac4a-11f0-bd47-4e936128c6c4/history
            ```
            
            **Not:** Process Instance ID'yi sipariş detayından alabilirsin
            """
    )
    @GetMapping("/{processInstanceId}/history")
    public ResponseEntity<List<TaskHistoryDto>> getProcessHistory(
            @Parameter(description = "Process Instance ID", example = "44bf4bea-ac4a-11f0-bd47-4e936128c6c4")
            @PathVariable String processInstanceId) {
        log.info("Süreç geçmişi sorgulanıyor - Process Instance ID: {}", processInstanceId);
        List<TaskHistoryDto> history = processService.getProcessHistory(processInstanceId);
        return ResponseEntity.ok(history);
    }
}
