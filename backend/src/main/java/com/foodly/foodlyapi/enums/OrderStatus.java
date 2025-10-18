package com.foodly.foodlyapi.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING(0, "Beklemede"),
    PAYMENT_APPROVED(1, "Ödeme Onaylandı"),
    ORDER_APPROVED(2, "Sipariş Onaylandı"),
    PREPARING(3, "Hazırlanıyor"),
    DELIVERED(4, "Teslim Edildi"),
    REJECTED(5, "Reddedildi");

    private final int index;
    private final String description;

    OrderStatus(int index, String description) {
        this.index = index;
        this.description = description;
    }
}
