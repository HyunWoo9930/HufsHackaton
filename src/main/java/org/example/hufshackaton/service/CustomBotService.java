package org.example.hufshackaton.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.example.hufshackaton.domain.Sports;
import org.example.hufshackaton.domain.Step;
import org.example.hufshackaton.repository.SportsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CustomBotService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final SportsRepository sportsRepository;

    public CustomBotService(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    public Sports saveSportsAndStep(String sports_name, String str) throws IOException {
        Sports sports = findSports(sports_name);
        List<String> steps = Arrays.stream(str.split("\n")).toList();
        if(sports == null) {
            Sports newSports = new Sports();
            newSports.setName(sports_name);
            for (String stepStr : steps) {
                Step step = new Step();
                step.setSports(newSports);
                step.setName(stepStr);
                String s = searchVideo(sports_name, stepStr);
                System.out.println("s = " + s);
                step.setYoutubeUrl(s);
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
                request -> {})
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
}
