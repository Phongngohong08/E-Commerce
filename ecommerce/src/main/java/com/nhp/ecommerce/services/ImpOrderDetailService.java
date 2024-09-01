package com.nhp.ecommerce.services;

import com.nhp.ecommerce.exceptions.DataNotFoundException;
import com.nhp.ecommerce.models.OrderDetail;
import com.nhp.ecommerce.repositories.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImpOrderDetailService implements OrderDetailService{
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail getOrderDetailById(Long id) {
        try {
            return orderDetailRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Order detail not found with id: " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
