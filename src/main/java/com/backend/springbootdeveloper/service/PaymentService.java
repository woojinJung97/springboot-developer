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

    @Transactional
    public void confirmPayment(PaymentDto dto) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // 토스 결제 승인 API 호출 준비
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

        // API 호출
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("토스 결제 승인 실패: " + response.body());
        }

        // 응답 파싱
        JsonNode jsonNode = objectMapper.readTree(response.body());
        String method = jsonNode.get("method").asText();
        String status = jsonNode.get("status").asText();
        String approvedAtStr = jsonNode.get("approvedAt").asText();

        // ISO 8601 날짜 파싱
        LocalDateTime approvedAt = OffsetDateTime.parse(approvedAtStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

        // DB 저장 order테이블
        Map<String, Object> orderMap = new HashMap();
        orderMap.put("orderId", dto.getOrderId());
        orderMap.put("userId", dto.getUserId());
        orderMap.put("email", email);
        orderMap.put("orderStatus", "PAID");

        paymentMapper.insertOrder(orderMap);

        // DB 저장 TossPayment 테이블
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("paymentId", UUID.randomUUID().toString());
        paymentMap.put("orderId", dto.getOrderId());
        paymentMap.put("tossOrderId", dto.getOrderId());
        paymentMap.put("tossPaymentKey", dto.getPaymentKey());
        paymentMap.put("method", method);
        paymentMap.put("status", status);
        paymentMap.put("requestedAt", LocalDateTime.now()); // 요청 시간
        paymentMap.put("approvedAt", approvedAt);
        paymentMap.put("totalAmount", dto.getAmount());

        paymentMapper.insertTossPayment(paymentMap);

        cancelPayment(dto.getPaymentKey(), "결제 환불~~(테스트)");
    }

    // 취소해서 돈 돌려받기
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

        if (response.statusCode() == 200) {
            System.out.println("결제 취소 성공");
        } else {
            throw new RuntimeException("결제 취소 실패: " + response.body());
        }
    }
}
