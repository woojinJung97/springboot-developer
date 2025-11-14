package com.backend.springbootdeveloper.dto;

import com.backend.springbootdeveloper.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainReservDto {

    private Long trainResvId;
    private Long userId;
    private Long seatId;
    private int trainNo;
    private String trainGrade;
    private String departStation;
    private String arriveStation;
    private LocalDate depDate;
    private LocalDate arrDate;
    private int price;
    private String promotion;
    private String reservState;
    private LocalDate regDate;
    private List<String> seatInfo;

}
