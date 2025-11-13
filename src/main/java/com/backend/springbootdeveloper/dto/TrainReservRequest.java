package com.backend.springbootdeveloper.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainReservRequest {

    private int trainno;
    private String trainGrade;
    private String depPlaceId;
    private String arrPlaceId;
    private String depPlandTime;

    private String promotion;
    private List<String> seatInfo;

}
