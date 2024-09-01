package com.nhp.ecommerce.responses;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String address;
    private String phoneNumber;
    private String note;
    private String paymentMethod;
    private String status;
    private LocalDateTime orderDate;
    private double total;
}
