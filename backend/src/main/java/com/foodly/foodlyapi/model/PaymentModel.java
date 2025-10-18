package com.foodly.foodlyapi.model;

import com.foodly.foodlyapi.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModel extends BaseModel {

    /**
     * İlgili sipariş ID'si
     */
    private Long orderId;

    /**
     * Ödeme tutarı
     */
    private BigDecimal amount;

    /**
     * Ödeme durumu (PENDING, COMPLETED, FAILED, REFUNDED)
     */
    private PaymentStatus status;

    /**
     * Ödeme yöntemi (Kredi Kartı, Nakit, vb.)
     */
    private String paymentMethod;

    /**
     * Ödeme sağlayıcısından alınan işlem ID'si
     */
    private String transactionId;

    /**
     * Ödemenin gerçekleştiği tarih ve saat
     */
    private LocalDateTime paymentDate;
}
