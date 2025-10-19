package com.foodly.foodlyapi.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    RESTAURANT(0, "Restoran"),
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
