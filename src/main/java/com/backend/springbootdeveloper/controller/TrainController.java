package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.dto.ApiResponse;
import com.backend.springbootdeveloper.dto.TrainItemDto;
import com.backend.springbootdeveloper.service.TrainApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/train")
public class TrainController {

    private final TrainApiService trainApiService;

    @GetMapping("/search")
    public ResponseEntity<List<TrainItemDto>> searchTrain(@RequestParam String depPlaceId,
                                                         @RequestParam String arrPlaceId,
                                                         @RequestParam String depPlandTime) {
        List<TrainItemDto> trains = trainApiService.getTrainData(depPlaceId, arrPlaceId, depPlandTime);

        return ResponseEntity.ok(trains);
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<TrainItemDto>> detailTrain(@RequestParam String depPlaceId,
                                                                 @RequestParam String arrPlaceId,
                                                                 @RequestParam String depPlandTime,
                                                                 @RequestParam int trainno) {
        TrainItemDto result = trainApiService.getDetailTrain(trainno, depPlaceId, arrPlaceId, depPlandTime);

        return ResponseEntity.ok(ApiResponse.success("열차 상세 조회 성공", result));
    }

}
