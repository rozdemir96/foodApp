package com.foodly.foodlyapi.service.delegate;

import com.foodly.foodlyapi.entity.Order;
import com.foodly.foodlyapi.enums.OrderStatus;
import com.foodly.foodlyapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * BPMN UserTask'ları tamamlandığında Order status'ünü güncelleyen listener.
 * Her UserTask başladığında veya bittiğinde tetiklenir.
 */
@Slf4j
@Component("orderStatusUpdateListener")
@RequiredArgsConstructor
public class OrderStatusUpdateListener implements ExecutionListener {
    private final OrderRepository orderRepository;

    @Override
    public void notify(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        String activityId = execution.getCurrentActivityId();
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        // Activity ID'ye göre Order status'ünü güncelle
        switch (activityId) {
            case "reviewOrder":
                // reviewOrder UserTask'ı başladı
                log.info("Sipariş onay aşamasında - Order ID: {}", orderId);
                break;
                
            case "prepareOrder":
                // prepareOrder UserTask'ı başladı
                order.setStatus(OrderStatus.PREPARING);
                orderRepository.save(order);
                log.info("Sipariş hazırlanıyor - Order ID: {}", orderId);
                break;
                
            case "shipOrder":
                // shipOrder UserTask'ı başladı (kuryeye verildi)
                log.info("Sipariş kuryede - Order ID: {}", orderId);
                break;
                
            case "endSuccess":
                // Süreç başarıyla tamamlandı
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                log.info("Sipariş teslim edildi - Order ID: {}", orderId);
                break;
                
            case "endRejected":
                // Süreç reddedildi (zaten PaymentApprovalDelegate'de REJECTED yapıldı)
                log.info("Sipariş süreci sonlandırıldı (RED) - Order ID: {}", orderId);
                break;
                
            default:
                log.debug("Activity geçişi: {} - Order ID: {}", activityId, orderId);
        }
    }
}
