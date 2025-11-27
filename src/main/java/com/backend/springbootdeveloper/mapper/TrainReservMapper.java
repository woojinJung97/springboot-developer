package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.dto.TrainReservDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrainReservMapper {
    void reservTrain(TrainReservDto reservDto);

    void insertTraintSeat(@Param("trainResvId")  Long trainResvId,@Param("seatInfo")  String seatInfo);

    boolean checkSeatReserved(@Param("trainno") int trainno,@Param("seatInfoList") List<String> seatInfo);

    List<String> getSeatInfo(@Param("trainno") int trainno);
}
