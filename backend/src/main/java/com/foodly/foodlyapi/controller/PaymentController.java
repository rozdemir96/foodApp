package com.foodly.foodlyapi.controller;

import com.foodly.foodlyapi.model.PaymentModel;
import com.foodly.foodlyapi.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Ödeme İşlemleri", description = "Ödeme kayıtlarını sorgulama ve güncelleme")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(
        summary = "Ödeme Oluştur (Manuel - Genelde Kullanılmaz)",
        description = """
            **Not:** Normal akışta PaymentApprovalDelegate otomatik ödeme kaydı oluşturur.
            Bu endpoint manuel test için kullanılabilir.
            """
    )
    @PostMapping
    public ResponseEntity<PaymentModel> createPayment(@RequestBody PaymentModel paymentModel) {
        PaymentModel created = paymentService.createPayment(paymentModel);
        return ResponseEntity.ok(created);
    }

    @Operation(
        summary = "Sipariş ID'sine Göre Ödeme Sorgula",
        description = """
            **Bir siparişin ödeme detaylarını getirir**
            
            **Örnek Kullanım:**
            ```
            GET /api/payments/order/1
            ```
            
            **Sonuç:** Ödeme tutarı, durum, işlem ID'si vb.
            """
    )
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentModel> getPaymentByOrderId(
            @Parameter(description = "Sipariş ID'si", example = "1")
            @PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Ödeme Durumunu Güncelle (Manuel)",
        description = """
            **Ödeme durumunu manuel olarak günceller**
            
            **Geçerli Durumlar:**
            - PENDING
            - APPROVED
            - REJECTED
            - FAILED
            
            **Örnek Kullanım:**
            ```
            PUT /api/payments/1/status?status=APPROVED
            ```
            """
    )
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentModel> updatePaymentStatus(
            @Parameter(description = "Ödeme ID'si", example = "1")
            @PathVariable Long paymentId,
            @Parameter(description = "Yeni durum", example = "APPROVED")
            @RequestParam String status) {
        PaymentModel updated = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(updated);
    }
}
