package com.backend.springbootdeveloper.dto;

import lombok.Data;

@Data
public class PaymentDto {

    private String paymentKey; // 토스 결제 키
    private String orderId; // 주문 ID
    private Long amount; // 결제 금액

    // 주문자 정보
    private Long userId;
    private String email;
    private String seats;
}
