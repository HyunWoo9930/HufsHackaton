package org.example.hufshackaton.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.hufshackaton.domain.Step;
import org.example.hufshackaton.service.SportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class SportsController {

    private final SportsService sportsService;

    public SportsController(SportsService sportsService) {
        this.sportsService = sportsService;
    }

    @GetMapping("get_all_sports")
    public ResponseEntity<?> getAllSports() {
        return ResponseEntity.ok(sportsService.getAllSports());
    }

    @GetMapping("get_steps")
    public ResponseEntity<?> getSteps(
            @RequestParam(value = "sports_name") String sports_name
    ) {
        Set<Step> steps = sportsService.getSteps(sports_name);
        return ResponseEntity.ok(steps);
    }
}
