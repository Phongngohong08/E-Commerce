package com.nhp.ecommerce.services;

import com.nhp.ecommerce.models.OrderDetail;

public interface OrderDetailService {
    OrderDetail getOrderDetailById(Long id) throws Exception;
}
