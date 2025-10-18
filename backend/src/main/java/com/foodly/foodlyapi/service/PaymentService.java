package com.foodly.foodlyapi.service;

import com.foodly.foodlyapi.model.PaymentModel;

import java.util.Optional;

public interface PaymentService {
    PaymentModel createPayment(PaymentModel paymentModel);
    
    Optional<PaymentModel> getPaymentByOrderId(Long orderId);
    
    PaymentModel updatePaymentStatus(Long paymentId, String status);
}
