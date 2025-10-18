package com.foodly.foodlyapi.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    CHEF(0, "Şef"),
    KITCHEN(1, "Mutfak Personeli"),
    DELIVERY(2, "Kurye"),
    ADMIN(3, "Yönetici");

    private final int index;
    private final String description;

    UserRole(int index, String description) {
        this.index = index;
        this.description = description;
    }
}
