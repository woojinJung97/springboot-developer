package com.backend.springbootdeveloper.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDto {

    private int trainno;
    private List<String> seatInfo;
    private String reservType; // 편도/왕복 구분

}
