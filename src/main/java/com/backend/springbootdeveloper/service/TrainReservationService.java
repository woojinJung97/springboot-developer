package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.TrainItemDto;
import com.backend.springbootdeveloper.dto.TrainReservDto;
import com.backend.springbootdeveloper.dto.TrainReservRequest;
import com.backend.springbootdeveloper.mapper.TrainReservMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TrainReservationService {

    private final TrainReservMapper trainReservMapper;
    private final TrainApiService trainApiService;

    @Transactional
    public TrainReservDto reservTrain(CustomUserDetails user, TrainReservRequest dto) {

        TrainItemDto trainItemDto = trainApiService.getDetailTrain(
                dto.getTrainno(),
                dto.getDepPlaceId(),
                dto.getArrPlaceId(),
                dto.getDepPlandTime()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        TrainReservDto reservDto = new TrainReservDto();
        reservDto.setTrainNo(dto.getTrainno());
        reservDto.setUserId(user.getUserId());
        reservDto.setTrainGrade(trainItemDto.getTraingradename());
        reservDto.setDepartStation(trainItemDto.getDepplacename());
        reservDto.setArriveStation(trainItemDto.getArrplacename());
        reservDto.setDepDate(LocalDate.parse(dto.getDepPlandTime(), formatter));
        reservDto.setArrDate(LocalDate.parse(dto.getDepPlandTime(), formatter));
        reservDto.setPrice(trainItemDto.getAdultcharge());
        reservDto.setPromotion(dto.getPromotion());
        reservDto.setReservState("예약 완료");
        reservDto.setSeatInfo(dto.getSeatInfo());
        reservDto.setRegDate(LocalDate.now());

        try {
            trainReservMapper.reservTrain(reservDto);

            if (dto.getSeatInfo() != null) {
                for (String seat : dto.getSeatInfo()) {
                    trainReservMapper.insertTraintSeat(reservDto.getTrainResvId(), seat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return reservDto;
    }
}
