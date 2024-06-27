package org.example.hufshackaton.repository;

import org.example.hufshackaton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
