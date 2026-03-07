package com.backend.springbootdeveloper.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface PaymentMapper {
    void insertOrder(Map<String, Object> orderMap);

    void insertTossPayment(Map<String, Object> paymentMap);

    void insertTrainSeat(Map<String, Object> seatMap);
    // 취소시 상태 변경
//    void updateOrderStatus(@Param("orderId") String orderId, @Param("status") String status);
}
