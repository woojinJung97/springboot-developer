package com.backend.springbootdeveloper.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainReservResponseDto {

    private int trainno;
    private String trainGrade;
    private String departStation;
    private LocalDate depDate;
    private LocalDate arrDate;
    private int price;
    private String promotion;
    private String reservState;
    private LocalDate regDate;
    private List<String> seatInfo = new ArrayList<>();

    @JsonIgnore
    private String seatInfoStr;

    // setter에서 변환
    public void setSeatInfoStr(String seatInfoStr) {
        System.out.println("setSeatInfoStr 호출됨: " + seatInfoStr); // 디버깅용
        if (seatInfoStr != null && !seatInfoStr.isEmpty()) {
            this.seatInfo = Arrays.asList(seatInfoStr.split(","));
        } else {
            this.seatInfo = new ArrayList<>();
        }
    }

}
