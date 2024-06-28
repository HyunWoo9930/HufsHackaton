package org.example.hufshackaton.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.hufshackaton.service.SportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
