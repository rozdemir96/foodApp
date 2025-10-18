package com.foodly.foodlyapi.mapper;

import com.foodly.foodlyapi.entity.Payment;
import com.foodly.foodlyapi.model.PaymentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentModel mapToModel(Payment payment);

    @Mapping(source = "orderId", target = "order.id")
    Payment mapToEntity(PaymentModel paymentModel);
}
