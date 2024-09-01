package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.OrderDTO;
import com.nhp.ecommerce.models.Order;

public interface OrderService {

    Order createOrder(OrderDTO orderDTO);
    Order getOrderById(Long id);
}
