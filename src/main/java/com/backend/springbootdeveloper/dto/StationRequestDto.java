package com.backend.springbootdeveloper.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StationRequestDto {

    private String cityCode;
    private String nodeid;
    private String nodename;

}
