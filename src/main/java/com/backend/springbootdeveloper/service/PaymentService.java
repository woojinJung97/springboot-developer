package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.dto.PaymentDto;
import com.backend.springbootdeveloper.mapper.PaymentMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Value("${toss.base-url}")
    private String tossBaseUrl;

    // 토스페이먼츠 결제 승인 API 호출 후 orders·toss_payment 테이블에 결제 정보 저장
    @Transactional
    public void confirmPayment(PaymentDto dto) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String authorization = "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentKey", dto.getPaymentKey());
        requestBody.put("orderId", dto.getOrderId());
        requestBody.put("amount", dto.getAmount());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tossBaseUrl + "/payments/confirm"))
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("토스 결제 승인 실패: " + response.body());
        }

        JsonNode jsonNode = objectMapper.readTree(response.body());
        String method = jsonNode.get("method").asText();
        String status = jsonNode.get("status").asText();
        LocalDateTime approvedAt = OffsetDateTime.parse(
                jsonNode.get("approvedAt").asText(),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
        ).toLocalDateTime();

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("orderId", dto.getOrderId());
        orderMap.put("userId", dto.getUserId());
        orderMap.put("email", email);
        orderMap.put("orderStatus", "PAID");
        paymentMapper.insertOrder(orderMap);

        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("paymentId", UUID.randomUUID().toString());
        paymentMap.put("orderId", dto.getOrderId());
        paymentMap.put("tossOrderId", dto.getOrderId());
        paymentMap.put("tossPaymentKey", dto.getPaymentKey());
        paymentMap.put("method", method);
        paymentMap.put("status", status);
        paymentMap.put("requestedAt", LocalDateTime.now());
        paymentMap.put("approvedAt", approvedAt);
        paymentMap.put("totalAmount", dto.getAmount());
        paymentMapper.insertTossPayment(paymentMap);
    }

    // 토스페이먼츠 결제 취소 API 호출 — 환불 처리
    public void cancelPayment(String paymentKey, String cancelReason) throws Exception {
        String authorization = "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cancelReason", cancelReason);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tossBaseUrl + "/payments/" + paymentKey + "/cancel"))
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("결제 취소 실패: " + response.body());
        }
    }
}
