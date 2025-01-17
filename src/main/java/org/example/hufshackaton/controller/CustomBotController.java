package org.example.hufshackaton.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.hufshackaton.domain.ChatGPTRequest;
import org.example.hufshackaton.domain.ChatGPTResponse;
import org.example.hufshackaton.domain.Sports;
import org.example.hufshackaton.service.CustomBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/bot")
@CrossOrigin(origins = "*")
public class CustomBotController {

    private final CustomBotService customBotService;

    @Value("${openai.model}")
    private String model;

    private String apiURL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate template;

    public CustomBotController(CustomBotService customBotService) {
        this.customBotService = customBotService;
    }

    @GetMapping("/sports_tendency_survey_chat")
    public ResponseEntity<?> chat(
            @RequestParam(name = "active") String active,
            @RequestParam(name = "teamwork") String teamwork,
            @RequestParam(name = "inout") String inout,
            @RequestParam(name = "force") String force,
            @RequestParam(name = "strategy") String strategy,
            @RequestParam(name = "culture") String culture,
            @RequestParam(name = "warning") String warning,
            @RequestParam(name = "technology") String technology,
            @RequestParam(name = "rule") String rule,
            @RequestParam(name = "interaction") String interaction
    ) throws IOException {
        ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제부터 내가 말한 조건을 생각해서, " +
                "[IMPORTANT] 전세계 스포츠 운동 중 한개를 선택해주는 운동 전문가야 " +
                "조건은 " +
                "활동 수준은 " + active + ", " +
                "팀워크 수준은 " + teamwork + ", " +
                "실내/실외는 " + inout + ", " +
                "운동 강도는 " + force + ", " +
                "전략적 사고는 " + strategy + ", " +
                "문화적 관심은 " + culture + ", " +
                "위험 감수는 " + warning + ", " +
                "기술 습득은 " + technology + ", " +
                "규칙 및 규율은 " + rule + ", " +
                "사회적 상호작용은 " + interaction + ", " +
                "이고 Temperature 는 0.2이야." +
                "다른 내용은 덧붙히지 말고 추천하는 운동만 얘기해줘." +
                "절대 뭐 괄호나 영어표현, 이런거 붙히지 말고 단어만 얘기해.");
        ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
        String sports_name = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        Sports sports = customBotService.findSports(sports_name);
        if (sports == null) {
            request = new ChatGPTRequest(model, "넌 이제 " + sports_name + "에 전문가야. 초심자가 너한테 물어봤을떄 " + sports_name + "을 10단계로 나눠서 알려줘. 다른건 전부 빼고 파싱하기 좋게 1부터 10까지 개행으로만 나누어서 적어줘");
            chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
            Sports sports1 = customBotService.saveSportsAndStep(sports_name, chatGPTResponse.getChoices().get(0).getMessage().getContent());
            return ResponseEntity.ok(sports1);
        } else {
            return ResponseEntity.ok(sports);
        }
    }

    @GetMapping("/search_sports")
    @Operation(summary = "스포츠를 검색하고, 없으면 물어본다.")
    public ResponseEntity<?> searchSports(
            @RequestParam(value = "sports_name") String sports_name
    ) throws IOException {
        Sports sports = customBotService.getSports(sports_name);
        return ResponseEntity.ok(sports);
    }


}