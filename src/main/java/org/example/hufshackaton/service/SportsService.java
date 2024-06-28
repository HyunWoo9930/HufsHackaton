package org.example.hufshackaton.service;

import org.example.hufshackaton.domain.Sports;
import org.example.hufshackaton.domain.Step;
import org.example.hufshackaton.repository.SportsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SportsService {
    private final SportsRepository sportsRepository;

    public SportsService(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    public List<Sports> getAllSports() {
        return sportsRepository.findAll();
    }

    public Set<Step> getSteps(String sports_name) {
        Sports sports = sportsRepository.searchByName(sports_name);
        return sports.getSteps();
    }
}
