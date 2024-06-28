package org.example.hufshackaton.controller;

import org.example.hufshackaton.domain.ChatGPTRequest;
import org.example.hufshackaton.domain.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class CustomBotController {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

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
                "운동은 한국(태권도), 일본(유도), 중국(탁구), 미국(농구), 영국(크리켓), 캐나다(아이스 하키), 프랑스(테니스), 스페인(축구), 포르투갈(풋살), 스웨덴(아이스 하키), 덴마크(핸드볼), 노르웨이(바이애슬론), 브라질(카포에이라), 아르헨티나(파토), 멕시코(차레리아) 15개중 한개로 골라줘야해." +
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

}