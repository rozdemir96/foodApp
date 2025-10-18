package com.foodly.foodlyapi.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(0, "Beklemede"),
    APPROVED(1, "Onaylandı"),
    REJECTED(2, "Reddedildi"),
    FAILED(3, "Başarısız");

    private final int index;
    private final String description;

    PaymentStatus(int index, String description) {
        this.index = index;
        this.description = description;
    }
}
