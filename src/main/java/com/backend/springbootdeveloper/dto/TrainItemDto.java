package com.backend.springbootdeveloper.dto;

import lombok.Data;

@Data
public class TrainItemDto {
    private int trainno;              // 열차번호
    private String traingradename;    // 열차종류 (KTX, 무궁화호 등)
    private String depplacename;      // 출발역
    private String arrplacename;      // 도착역
    private long depplandtime;        // 출발일시
    private long arrplandtime;        // 도착일시
    private int adultcharge;          // 요금
}
