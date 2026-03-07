package com.backend.springbootdeveloper.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservDto {

    private String reservType; // "ONE_WAY" 또는 "ROUND_TRIP"
    private int trainno;
    private String depPlaceId;
    private String arrPlaceId;
    private String depPlandTime;
    private String arrPlandTime;
    private List<String> seatInfo;

}
