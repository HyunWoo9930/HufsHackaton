package org.example.hufshackaton.service;

import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.example.hufshackaton.domain.User;
import org.example.hufshackaton.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

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

    public void createUser(String user_id, String password) {
        User user = new User();
        user.setUserId(user_id);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String user_id) {
        userRepository.deleteUserByUserId(user_id);
    }

    public User getUser(String user_id) {
        return userRepository.findByUserId(user_id);
    }

    public void updatePassword(String user_id, String password, String new_password) throws BadRequestException {
        User user = getUser(user_id);
        if (user == null) {
            throw new NotFoundException("user가 존재하지 않습니다.");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(new_password));
            userSave(user);
        } else {
            throw new BadRequestException("현재 비밀번호와 같지않습니다.");
        }
        if (!passwordEncoder.matches(new_password, getUser(user_id).getPassword())) {
            throw new RuntimeException("비밀번호가 변경되지 않았습니다.");
        }
    }

    public void userSave(User user) {
        userRepository.save(user);
    }
}
