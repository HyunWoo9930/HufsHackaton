package org.example.hufshackaton.service;

import org.example.hufshackaton.domain.Sports;
import org.example.hufshackaton.repository.SportsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportsService {
    private final SportsRepository sportsRepository;

    public SportsService(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    public List<Sports> getAllSports() {
        return sportsRepository.findAll();
    }
}
