package org.example.hufshackaton.service;

import org.example.hufshackaton.domain.User;
import org.example.hufshackaton.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getTotalUsers() {
        return userRepository.findAll();
    }
}
