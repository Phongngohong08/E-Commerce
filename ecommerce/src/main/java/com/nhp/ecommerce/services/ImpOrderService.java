package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.CartItemDTO;
import com.nhp.ecommerce.dtos.OrderDTO;
import com.nhp.ecommerce.exceptions.DataNotFoundException;
import com.nhp.ecommerce.models.*;
import com.nhp.ecommerce.repositories.OrderDetailRepository;
import com.nhp.ecommerce.repositories.OrderRepository;
import com.nhp.ecommerce.repositories.ProductRepository;
import com.nhp.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpOrderService implements OrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;


    @Override
    public Order createOrder(OrderDTO orderDTO) {
        User user;
        try {
            user = userRepository
                    .findById(orderDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
        Order order = Order.builder()
                .user(user)
                .phoneNumber(user.getPhoneNumber())
                .address(orderDTO.getAddress())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .note(orderDTO.getNote())
                .paymentMethod(orderDTO.getPaymentMethod())
                .build();

        double total = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();

            Long productId = cartItemDTO.getProductId();
            Product product = null;
            try {
                product = productRepository.findById(productId)
                        .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));
            } catch (DataNotFoundException e) {
                throw new RuntimeException(e);
            }
            orderDetail.setProductId(productId);
            orderDetail.setQuantity(cartItemDTO.getQuantity());
            orderDetails.add(orderDetail);
            total += product.getPrice() * cartItemDTO.getQuantity();
        }
        order.setTotal(total);
        orderRepository.save(order);

        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrder(order);
        }
        orderDetailRepository.saveAll(orderDetails);

        return order;
    }

    @Override
    public Order getOrderById(Long id){
        try {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
