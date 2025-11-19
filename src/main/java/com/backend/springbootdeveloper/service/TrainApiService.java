package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.dto.StationRequestDto;
import com.backend.springbootdeveloper.dto.TrainItemDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.net.URI;

@Service
public class TrainApiService {

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

            // RestTemplate가 추가 인코딩 못 하도록 URI로 호출
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
            throw new RuntimeException("열차 조회 중 오류 발생", e);
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

    public List<StationRequestDto> searchTrainStation() {
        String cityCode = "11";
        String encodedKey = URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8);
        String url = "https://apis.data.go.kr/1613000/TrainInfoService/getCtyAcctoTrainSttnList"
                + "?serviceKey=" + encodedKey
                + "&cityCode=" + cityCode
                + "&numOfRows=10&pageNo=1&_type=json";

        try {
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
}


