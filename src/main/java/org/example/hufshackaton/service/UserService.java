package org.example.hufshackaton.service;

import org.example.hufshackaton.domain.User;
import org.example.hufshackaton.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getTotalUsers() {
        return userRepository.findAll();
    }

    public void createUser(String user_id, String user_name, String password, String email) {
        User user = new User();
        user.setUserId(user_id);
        user.setName(user_name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
