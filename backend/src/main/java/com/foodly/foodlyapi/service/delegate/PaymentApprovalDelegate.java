package com.foodly.foodlyapi.service.delegate;

import com.foodly.foodlyapi.entity.Order;
import com.foodly.foodlyapi.entity.Payment;
import com.foodly.foodlyapi.enums.OrderStatus;
import com.foodly.foodlyapi.enums.PaymentStatus;
import com.foodly.foodlyapi.repository.OrderRepository;
import com.foodly.foodlyapi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * BPMN sürecinde ödeme onaylama adımını simüle eden delegate servisi.
 * %70 olasılıkla ödemeyi onaylar, %30 olasılıkla reddeder.
 * Ödeme sonucunu Payment tablosuna kaydeder ve Order status'ünü günceller.
 */
@Slf4j
@Service("paymentApprovalDelegate")
@RequiredArgsConstructor
public class PaymentApprovalDelegate implements JavaDelegate {
    private final Random random = new Random();
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution execution) {
        // BPMN sürecinden orderId değişkenini al
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        // %70 ihtimalle ödeme başarılı
        boolean paymentApproved = random.nextInt(100) < 70;
        
        // Payment kaydı oluştur
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(100.00)); // Simülasyon için sabit tutar
        payment.setPaymentMethod("Kredi Kartı");
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        
        if (paymentApproved) {
            // Ödeme başarılı
            payment.setStatus(PaymentStatus.APPROVED);
            payment.setPaymentDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PAYMENT_APPROVED);
            
            log.info("Ödeme onaylandı - Order ID: {}, Payment ID: {}", orderId, payment.getId());
        } else {
            // Ödeme reddedildi
            payment.setStatus(PaymentStatus.REJECTED);
            order.setStatus(OrderStatus.REJECTED);
            
            log.warn("Ödeme reddedildi - Order ID: {}", orderId);
        }
        
        // Veritabanına kaydet
        paymentRepository.save(payment);
        orderRepository.save(order);
        
        // BPMN sürecine paymentApproved değişkenini set et
        // Bu değişken, BPMN'deki paymentApprovedGateway'de karar vermek için kullanılır
        execution.setVariable("paymentApproved", paymentApproved);
    }
}
