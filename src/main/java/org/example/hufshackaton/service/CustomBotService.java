package org.example.hufshackaton.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.example.hufshackaton.domain.ChatGPTRequest;
import org.example.hufshackaton.domain.ChatGPTResponse;
import org.example.hufshackaton.domain.Sports;
import org.example.hufshackaton.domain.Step;
import org.example.hufshackaton.repository.SportsRepository;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CustomBotService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private String apiURL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate template;

    private final SportsRepository sportsRepository;

    public CustomBotService(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    public Sports saveSportsAndStep(String sports_name, String str) throws IOException {
        Sports sports = findSports(sports_name);
        List<String> steps = Arrays.stream(str.split("\n")).toList();
        if (sports == null) {
            Sports newSports = new Sports();
            newSports.setName(sports_name);
            ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제 " + sports_name + "에 전문가야. 초심자한테 " + sports_name + "에 관해서 간단하게 두줄정도만 설명하줘 다른 말은 하지마.");
            ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
            String description = chatGPTResponse.getChoices().get(0).getMessage().getContent();
            newSports.setDescription(description);
            String imgUrl = getImgUrl(sports_name);
            newSports.setImageUrl(imgUrl);
            for (String stepStr : steps) {
                ChatGPTRequest stepRequest = new ChatGPTRequest(model, "운동 종류는 " + sports_name + "이고, 운동 단계는 " + stepStr + "이 단계를 두줄 정도로 요약해줘. 요약 말고 다른 말은 하지말아줘.");
                ChatGPTResponse stepChatGPTResponse = template.postForObject(apiURL, stepRequest, ChatGPTResponse.class);
                String stepDescription = stepChatGPTResponse.getChoices().get(0).getMessage().getContent();
                Step step = new Step();
                step.setDescription(stepDescription);
                step.setSports(newSports);
                step.setName(stepStr);
//                String video = searchVideo(sports_name, stepStr);
//                step.setYoutubeUrl(video);
                newSports.addStep(step);
            }
            return saveSports(newSports);
        } else {
            return sports;
        }
    }

    public Sports findSports(String sports_name) {
        return sportsRepository.findByName(sports_name);
    }

    public Sports saveSports(Sports sports) {
        return sportsRepository.save(sports);
    }

    public String searchVideo(String sports_name, String query) throws IOException {
        // JSON 데이터를 처리하기 위한 JsonFactory 객체 생성
        JsonFactory jsonFactory = new JacksonFactory();

        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {
                })
                .build();

        // YouTube Search API를 사용하여 동영상 검색을 위한 요청 객체 생성
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));

        // API 키 설정
        search.setKey(apiKey);

        // 검색어 설정
        search.setQ(sports_name + query);

        // 검색 요청 실행 및 응답 받아오기
        SearchListResponse searchResponse = search.execute();

        // 검색 결과에서 동영상 목록 가져오기
        List<SearchResult> searchResultList = searchResponse.getItems();

        if (searchResultList != null && searchResultList.size() > 0) {
            SearchResult searchResult = searchResultList.get(0);
            String videoId = searchResult.getId().getVideoId();
            return "https://www.youtube.com/watch?v=" + videoId;
        }
        return "검색 결과가 없습니다";
    }

    public String getImgUrl(String name) {
        String imageUrl = "";
        try {
            Connection.Response res = Jsoup.connect(
                            "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=1295c887390fe450d&q=운동 " + name + " 하는 사진")
                    .ignoreContentType(true).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").execute();
            JSONObject json;

            json = new JSONObject(res.body());
            imageUrl =
                    json.getJSONArray("items").getJSONObject(0).getJSONObject("pagemap").getJSONArray("cse_thumbnail").getJSONObject(0).getString("src");

        } catch (Exception e) {
            imageUrl = "";
        }
        return imageUrl;
    }
}
