package com.foodly.foodlyapi.mapper;

import com.foodly.foodlyapi.entity.Order;
import com.foodly.foodlyapi.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderModel mapToModel(Order order);

    Order mapToEntity(OrderModel orderModel);
}
