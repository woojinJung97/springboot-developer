package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.dto.PaymentDto;
import com.backend.springbootdeveloper.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @RequestMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmPayment(@RequestBody PaymentDto dto) {
        try {
            paymentService.confirmPayment(dto);
            return ResponseEntity.ok(new ApiResponse<>(200,"결제 성공", "OK"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "결제 실패", null));
        }
    }

}
