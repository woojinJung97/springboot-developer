package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.dto.SeatRequestDto;
import com.backend.springbootdeveloper.dto.SeatResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainReservMapper {
    List<SeatResponseDto> getSeatInfo(int trainno, SeatRequestDto dto);

    boolean checkedSeat(int trainno, List<String> seatInfo);

    void reservTrain(SeatResponseDto reservDto);

    void insertTrainSeats(Long trainResvId, String seat);
}
