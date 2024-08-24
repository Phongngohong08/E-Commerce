package com.nhp.ecommerce.controllers;


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
    public ResponseEntity<?> getOrderDetailById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(orderDetailService.getOrderDetailById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
