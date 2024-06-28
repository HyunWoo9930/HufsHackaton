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
    ) {
        ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제부터 내가 말한 조건을 생각해서, " +
                "[IMPORTANT] 전세계 스포츠 운동 중 한개를 선택해주는 운동 전문가야 " +
                "다른 내용은 덧붙히지 말고 추천하는 운동만 얘기해줘. 조건은 " +
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
                "이고 Temperature 는 0.2이야.");
        ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
        return ResponseEntity.ok(chatGPTResponse.getChoices().get(0).getMessage().getContent());
    }

    @GetMapping("/get_sports")
    @Operation(summary = "만약 DB에 존재하는 이름이면 그 스포츠를 반환해주고, 없으면 새로 생성하여 반환해주는 API")
    public ResponseEntity<?> createNewSports(
            @RequestParam(value = "sports_name") String sports_name
    ) throws IOException {
        Sports sports = customBotService.findSports(sports_name);
        if(sports == null) {
        ChatGPTRequest request = new ChatGPTRequest(model, "넌 이제 " + sports_name + "에 전문가야. 초심자가 너한테 물어봤을떄 " + sports_name + "을 10단계로 나눠서 알려줘. 다른건 전부 빼고 파싱하기 좋게 1부터 10까지 개행으로만 나누어서 적어줘");
        ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
            Sports sports1 = customBotService.saveSportsAndStep(sports_name, chatGPTResponse.getChoices().get(0).getMessage().getContent());
            return ResponseEntity.ok(sports1);
        } else {
            return ResponseEntity.ok(sports);
        }
    }

}