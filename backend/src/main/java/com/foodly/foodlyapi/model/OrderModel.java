package com.foodly.foodlyapi.model;

import com.foodly.foodlyapi.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderModel extends BaseModel {

    /**
     * Siparişi veren kullanıcının ID'si
     */
    private Long userId;

    /**
     * Sipariş verilen restoranın ID'si
     */
    private Long restaurantId;

    /**
     * Siparişin güncel durumu (BPMN sürecindeki adım)
     * PENDING -> PAYMENT_APPROVED -> ORDER_APPROVED -> PREPARING -> DELIVERED
     * veya herhangi bir aşamada REJECTED olabilir
     */
    private OrderStatus status;

    /**
     * Siparişin oluşturulma tarihi ve saati
     */
    private LocalDateTime orderDate;

    /**
     * Flowable BPMN sürecinin instance ID'si
     * Bu ID ile BPMN sürecini takip edebilir ve yönetebiliriz
     */
    private String processInstanceId;
}
