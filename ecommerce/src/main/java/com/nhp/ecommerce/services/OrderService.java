package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.OrderDTO;
import com.nhp.ecommerce.models.Order;

public interface OrderService {

    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrderById(Long id) throws Exception;
}
