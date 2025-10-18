package com.foodly.foodlyapi.service.impl;

import com.foodly.foodlyapi.entity.Payment;
import com.foodly.foodlyapi.enums.PaymentStatus;
import com.foodly.foodlyapi.mapper.PaymentMapper;
import com.foodly.foodlyapi.model.PaymentModel;
import com.foodly.foodlyapi.repository.PaymentRepository;
import com.foodly.foodlyapi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentModel createPayment(PaymentModel paymentModel) {
        Payment payment = paymentMapper.mapToEntity(paymentModel);
        payment.setStatus(PaymentStatus.PENDING);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Ödeme kaydı oluşturuldu - Payment ID: {}, Order ID: {}", 
                savedPayment.getId(), savedPayment.getOrder().getId());
        
        return paymentMapper.mapToModel(savedPayment);
    }

    @Override
    public Optional<PaymentModel> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(paymentMapper::mapToModel);
    }

    @Override
    @Transactional
    public PaymentModel updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        payment.setStatus(paymentStatus);
        
        if (paymentStatus == PaymentStatus.APPROVED) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Ödeme durumu güncellendi - Payment ID: {}, Durum: {}", 
                paymentId, status);
        
        return paymentMapper.mapToModel(updatedPayment);
    }
}
