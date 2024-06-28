package org.example.hufshackaton.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
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

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    private final SportsRepository sportsRepository;

    public CustomBotService(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    public Sports saveSportsAndStep(String sportsName, String stepsDescription) throws IOException {
        Sports sports = findSports(sportsName);

        if (sports == null) {
            sports = createNewSports(sportsName);
            addStepsToSports(sports, stepsDescription);
            return saveSports(sports);
        }
        return sports;
    }

    private Sports createNewSports(String sportsName) {
        Sports sports = new Sports();
        sports.setName(sportsName);
        sports.setDescription(fetchDescriptionFromChatGPT(sportsName));
        sports.setImageUrl(fetchImageUrl(sportsName));
        sports.setCountry(fetchCountryFromChatGPT(sportsName));
        return sports;
    }

    private void addStepsToSports(Sports sports, String stepsDescription) throws IOException {
        List<String> steps = Arrays.asList(stepsDescription.split("\n"));
        for (String stepStr : steps) {
            Step step = createStep(sports, stepStr);
            sports.addStep(step);
        }
    }

    private Step createStep(Sports sports, String stepStr) throws IOException {
        Step step = new Step();
        step.setName(stepStr);
        step.setDescription(fetchStepDescriptionFromChatGPT(sports.getName(), stepStr));
        step.setSports(sports);
        step.setYoutubeUrl(searchVideo(sports.getName(), stepStr));
        return step;
    }

    public Sports findSports(String sportsName) {
        return sportsRepository.findByName(sportsName);
    }

    public Sports saveSports(Sports sports) {
        return sportsRepository.save(sports);
    }

    public String searchVideo(String sportsName, String query) throws IOException {
        YouTube youtube = new YouTube.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), new JacksonFactory(), request -> {}).build();
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
        search.setKey(apiKey);
        search.setQ(sportsName + " " + query);

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        if (searchResultList != null && !searchResultList.isEmpty()) {
            return "https://www.youtube.com/watch?v=" + searchResultList.get(0).getId().getVideoId();
        }
        return "검색 결과가 없습니다";
    }

    public String fetchImageUrl(String sportsName) {
        try {
            Connection.Response res = Jsoup.connect("https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=1295c887390fe450d&q=운동 " + sportsName + " 하는 사진")
                    .ignoreContentType(true).userAgent("Mozilla/5.0").execute();
            JSONObject json = new JSONObject(res.body());
            return json.getJSONArray("items").getJSONObject(0).getJSONObject("pagemap").getJSONArray("cse_thumbnail").getJSONObject(0).getString("src");
        } catch (Exception e) {
            return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjBEWRP1Kx49IU8pKabdFyKHR1dW7Wn6O0zg&s";
        }
    }

    public Sports getSports(String sportsName) throws IOException {
        Sports sports = sportsRepository.searchByName(sportsName);

        if (sports == null) {
            String stepsDescription = fetchStepsDescriptionFromChatGPT(sportsName);
            return saveSportsAndStep(sportsName, stepsDescription);
        }
        return sports;
    }

    private String fetchDescriptionFromChatGPT(String sportsName) {
        ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제 " + sportsName + "에 전문가야. 초심자한테 " + sportsName + "에 관해서 간단하게 두줄정도만 설명해줘 다른 말은 하지마.");
        return postToChatGPT(request).getChoices().get(0).getMessage().getContent();
    }

    private String fetchCountryFromChatGPT(String sportsName) {
        ChatGPTRequest request = new ChatGPTRequest(model, sportsName + "은 어디나라 운동이야? 다른 말은 하지말고 어디 나라인지만 적어줘. 나라가 많으면 그냥 만국공통이라고만 적어줘.");
        return postToChatGPT(request).getChoices().get(0).getMessage().getContent();
    }

    private String fetchStepDescriptionFromChatGPT(String sportsName, String step) {
        ChatGPTRequest request = new ChatGPTRequest(model, "운동 종류는 " + sportsName + "이고, 운동 단계는 " + step + "이 단계를 두줄 정도로 요약해줘. 요약 말고 다른 말은 하지말아줘.");
        return postToChatGPT(request).getChoices().get(0).getMessage().getContent();
    }

    private String fetchStepsDescriptionFromChatGPT(String sportsName) {
        ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제 " + sportsName + "에 전문가야. 초심자가 너한테 물어봤을때 " + sportsName + "을 10단계로 나눠서 알려줘. 다른건 전부 빼고 파싱하기 좋게 1부터 10까지 개행으로만 나누어서 적어줘");
        return postToChatGPT(request).getChoices().get(0).getMessage().getContent();
    }

    private ChatGPTResponse postToChatGPT(ChatGPTRequest request) {
        return restTemplate.postForObject(API_URL, request, ChatGPTResponse.class);
    }
}
