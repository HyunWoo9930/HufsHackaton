package org.example.hufshackaton.repository;

import org.example.hufshackaton.domain.Sports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportsRepository extends JpaRepository<Sports, Long> {
    Sports findByName(String sports_name);
}
