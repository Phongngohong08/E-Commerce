package com.nhp.ecommerce.controllers;

import com.nhp.ecommerce.dtos.OrderDTO;
import com.nhp.ecommerce.models.Order;
import com.nhp.ecommerce.responses.ApiResponse;
import com.nhp.ecommerce.responses.OrderResponse;
import com.nhp.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderDTO orderDTO,
                                         BindingResult result) {
        LOGGER.info("Creating order: {}", orderDTO);
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ApiResponse.<OrderResponse>builder()
                    .message(errorMessages.toString())
                    .build();
        }
        Order order = orderService.createOrder(orderDTO);

        return ApiResponse.<OrderResponse>builder()
                .result(OrderResponse.builder()
                        .id(order.getId())
                        .address(order.getAddress())
                        .phoneNumber(order.getPhoneNumber())
                        .note(order.getNote())
                        .paymentMethod(order.getPaymentMethod())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .total(order.getTotal())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable long id) {

        LOGGER.info("Getting order by id: {}", id);
        Order order = orderService.getOrderById(id);

        return ApiResponse.<OrderResponse>builder()
                .result(OrderResponse.builder()
                        .id(order.getId())
                        .address(order.getAddress())
                        .phoneNumber(order.getPhoneNumber())
                        .note(order.getNote())
                        .paymentMethod(order.getPaymentMethod())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .total(order.getTotal())
                        .build())
                .build();
    }
}
