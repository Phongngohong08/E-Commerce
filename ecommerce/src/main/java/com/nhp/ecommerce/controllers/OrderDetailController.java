package com.nhp.ecommerce.controllers;


import com.nhp.ecommerce.models.OrderDetail;
import com.nhp.ecommerce.responses.ApiResponse;
import com.nhp.ecommerce.responses.OrderDetailResponse;
import com.nhp.ecommerce.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orderdetails")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> getOrderDetailById(@PathVariable long id) {

        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);

        return ApiResponse.<OrderDetailResponse>builder()
                .result(OrderDetailResponse.builder()
                        .id(orderDetail.getId())
                        .productId(orderDetail.getProductId())
                        .orderId(orderDetail.getOrder().getId())
                        .quantity(orderDetail.getQuantity())
                        .build())
                .build();
    }
}
