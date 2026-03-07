package com.backend.springbootdeveloper.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationRequestDto {

    private String cityCode;
    private String nodeid;
    private String nodename;

}
