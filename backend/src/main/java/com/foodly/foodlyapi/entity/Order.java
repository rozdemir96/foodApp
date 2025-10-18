package com.foodly.foodlyapi.entity;

import com.foodly.foodlyapi.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    /**
     * Siparişi veren kullanıcının ID'si
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Sipariş verilen restoranın ID'si
     */
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    /**
     * Siparişin güncel durumu (BPMN sürecindeki adım)
     * PENDING -> PAYMENT_APPROVED -> ORDER_APPROVED -> PREPARING -> DELIVERED
     * veya herhangi bir aşamada REJECTED olabilir
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    /**
     * Siparişin oluşturulma tarihi ve saati
     */
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    /**
     * Flowable BPMN sürecinin instance ID'si
     * Bu ID ile BPMN sürecini takip edebilir ve yönetebiliriz
     */
    @Column(name = "process_instance_id")
    private String processInstanceId;
}
