package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.*;
import com.backend.springbootdeveloper.mapper.TrainReservMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainApiService {

    private final TrainReservMapper trainReservMapper;

    @Value("${train.api.key}")
    private String apiKey;

    public List<TrainItemDto> getTrainData(String depPlaceId, String arrPlaceId, String depPlandTime) {
        try {
            String encodedKey = URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8);
            
            String url = "https://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo"
                    + "?serviceKey=" + encodedKey
                    + "&depPlaceId=" + depPlaceId
                    + "&arrPlaceId=" + arrPlaceId
                    + "&depPlandTime=" + depPlandTime
                    + "&numOfRows=10&pageNo=1&_type=json";
            
            // RestTemplate 추가 인코딩 못하게 URI로 호출
            String response = new RestTemplate().getForObject(new URI(url), String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");
            
            List<TrainItemDto> result = new ArrayList<>();
            for (JsonNode node : items) {
                TrainItemDto dto = mapper.treeToValue(node, TrainItemDto.class);
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("열차 조회 중 오류 발생" + e);
        }
    }

    public TrainItemDto getDetailTrain(int trainno, String depPlaceId, String arrPlaceId, String depPlandTime) {
        try {
            String encodedKey = URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8);

            String url = "https://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo"
                    + "?serviceKey=" + encodedKey
                    + "&depPlaceId=" + depPlaceId
                    + "&arrPlaceId=" + arrPlaceId
                    + "&depPlandTime=" + depPlandTime
                    + "&_type=json";

            // RestTemplate가 추가 인코딩 못 하도록 URI로 호출
            String response = new RestTemplate().getForObject(new URI(url), String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    int currentTrainNo = itemNode.path("trainno").asInt();
                    if (currentTrainNo == trainno) {
                        return mapper.treeToValue(itemNode, TrainItemDto.class);
                    }
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<StationRequestDto> getStationName() {
            String cityCode = "11";
            String encodedKey = URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8);

            String url = "https://apis.data.go.kr/1613000/TrainInfoService/getCtyAcctoTrainSttnList"
                    + "?serviceKey=" + encodedKey
                    + "&cityCode=" + cityCode
                    + "&_type=json";

        try{
            String response = new RestTemplate().getForObject(new URI(url), String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            List<StationRequestDto> result = new ArrayList<>();
            for (JsonNode node : items) {
                StationRequestDto dto = mapper.treeToValue(node, StationRequestDto.class);
                result.add(dto);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SeatResponseDto> getSeatInfo(int trainno, SeatRequestDto dto) {
        List<SeatResponseDto> result = trainReservMapper.getSeatInfo(trainno, dto);

        return result;
    }

    @Transactional
    public SeatResponseDto reservSeats(CustomUserDetails user, SeatReservDto dto) {
        TrainItemDto trainDto = getDetailTrain(
                dto.getTrainno(),
                dto.getDepPlaceId(),
                dto.getArrPlaceId(),
                dto.getDepPlandTime()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        SeatResponseDto reservDto = new SeatResponseDto();
        reservDto.setUserId(user.getUserId());
        reservDto.setTrainno(dto.getTrainno());
        reservDto.setDepartStation(trainDto.getDepplacename());
        reservDto.setArriveStation(trainDto.getArrplacename());
        reservDto.setArrDate(LocalDate.parse(dto.getArrPlandTime(), formatter));
        reservDto.setDepDate(LocalDate.parse(dto.getDepPlandTime(), formatter));
        reservDto.setPrice(trainDto.getAdultcharge());
        reservDto.setTraingradename(trainDto.getTraingradename());
        reservDto.setSeatInfo(dto.getSeatInfo());
        reservDto.setReservState("예약 완료");
        reservDto.setRegDate(LocalDate.now());

        try {
            if (trainReservMapper.checkedSeat(dto.getTrainno(), dto.getSeatInfo())) {
                throw new IllegalArgumentException("이미 예약된 좌석입니다.");
            }

            trainReservMapper.reservTrain(reservDto);

            if (dto.getSeatInfo() != null) {
                for (String seat : dto.getSeatInfo()) {
                    trainReservMapper.insertTrainSeats(reservDto.getTrainResvId(), seat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return reservDto;
    }
}
