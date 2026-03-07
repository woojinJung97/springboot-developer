package com.backend.springbootdeveloper.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {

    private int trainno;              // 열차번호
    private Long trainResvId;
    private Long userId;
    private String traingradename;    // 열차종류 (KTX, 무궁화호 등)
    private String departStation;      // 출발역
    private String arriveStation;      // 도착역
    private LocalDate depDate;        // 출발일시
    private LocalDate arrDate;        // 도착일시
    private int price;          // 요금
    private List<String> seatInfo;    // 좌석 정보
    private String reservState;
    private LocalDate regDate;

}
