package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.*;
import com.backend.springbootdeveloper.service.TrainApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/train")
public class TrainController {

    private final TrainApiService trainApiService;

    // 기차 조회
    @GetMapping("/search")
    public ResponseEntity<List<TrainItemDto>> searchTrain(@RequestParam String depPlaceId,
                                                         @RequestParam String arrPlaceId,
                                                         @RequestParam String depPlandTime) {
        List<TrainItemDto> trains = trainApiService.getTrainData(depPlaceId, arrPlaceId, depPlandTime);

        return ResponseEntity.ok(trains);
    }

    // 기차 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<TrainItemDto>> detailTrain(@RequestParam String depPlaceId,
                                                                 @RequestParam String arrPlaceId,
                                                                 @RequestParam String depPlandTime,
                                                                 @RequestParam int trainno) {
        TrainItemDto result = trainApiService.getDetailTrain(trainno, depPlaceId, arrPlaceId, depPlandTime);

        return ResponseEntity.ok(ApiResponse.success("열차 상세 조회 성공", result));
    }

    // 기차역 조회
    @GetMapping("/train-station")
    public ResponseEntity<ApiResponse<List<StationRequestDto>>> getStationName() {
        List<StationRequestDto> result = trainApiService.getStationName();

        return ResponseEntity.ok(ApiResponse.success("도시명 조회 성공", result));
    }

    // 좌석 조회
    @GetMapping("/seat-info")
    public ResponseEntity<ApiResponse<List<SeatResponseDto>>> getSeatInfo(@RequestParam int trainno, SeatRequestDto dto) {
        List<SeatResponseDto> result = trainApiService.getSeatInfo(trainno, dto);

        return ResponseEntity.ok(ApiResponse.success("좌석 조회에 성공했습니다.", result));
    }

    // 좌석 예약
    @PostMapping("/reserv-seats")
    public ResponseEntity<ApiResponse<SeatResponseDto>> reserveSeats(@AuthenticationPrincipal CustomUserDetails user, SeatReservDto dto) {
        SeatResponseDto result = trainApiService.reservSeats(user, dto);

        return ResponseEntity.ok(ApiResponse.success("좌석예약이 완료되었습니다.", result));
    }


}
