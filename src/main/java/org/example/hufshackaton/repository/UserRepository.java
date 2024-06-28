package org.example.hufshackaton.repository;

import org.example.hufshackaton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    void deleteUserByUserId(String user_id);

    User findByUserId(String user_id);
}
