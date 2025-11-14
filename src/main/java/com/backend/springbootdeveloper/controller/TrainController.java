package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.dto.TrainItemDto;
import com.backend.springbootdeveloper.dto.TrainReservDto;
import com.backend.springbootdeveloper.dto.TrainReservRequest;
import com.backend.springbootdeveloper.service.TrainApiService;
import com.backend.springbootdeveloper.service.TrainReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainApiService trainApiService;
    private final TrainReservationService trainReservationService;

    // 기차 조회
    @GetMapping("/search")
    public ResponseEntity<List<TrainItemDto>> searchTrain(@RequestParam String depPlaceId,
                                                          @RequestParam String arrPlaceId,
                                                          @RequestParam String depPlandTime) {
        List<TrainItemDto> trains = trainApiService.getTrainData(depPlaceId, arrPlaceId, depPlandTime);
        return ResponseEntity.ok(trains);
    }

    // 기차 상세조회
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<TrainItemDto>> detailTrain(@RequestParam String depPlaceId,
                                                                 @RequestParam String arrPlaceId,
                                                                 @RequestParam String depPlandTime,
                                                                 @RequestParam int trainno) {
        TrainItemDto dto = trainApiService.getDetailTrain(trainno,  depPlaceId, arrPlaceId, depPlandTime);

        return ResponseEntity.ok(ApiResponse.success("열차 상세 조회 성공", dto));
    }

    // 기차 예약
    @PostMapping("/reserv/train")
    public ResponseEntity<ApiResponse<TrainReservDto>> reservTrain(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TrainReservRequest trainReservDto) {
        TrainReservDto response = trainReservationService.reservTrain(user, trainReservDto);

        return ResponseEntity.ok(ApiResponse.success("기차 예약이 완료되었습니다.", response));
    }
}
