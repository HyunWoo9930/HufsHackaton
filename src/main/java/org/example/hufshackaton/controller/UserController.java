package org.example.hufshackaton.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.hufshackaton.domain.User;
import org.example.hufshackaton.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get_total_users")
    public ResponseEntity<?> getTotalUsers() {
        List<User> users = userService.getTotalUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            userService.createUser(user.getUserId(), user.getPassword());
            return ResponseEntity.ok().body("ok");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry for user ID or email.");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data Access Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "userId") String user_id) {
        if (userService.getUser(user_id) != null) {
            userService.deleteUser(user_id);
            if (userService.getUser(user_id) == null) {
                return ResponseEntity.ok().body("ok");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("user가 삭제되지 않았습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user가 존재하지 않습니다.");
    }

    @GetMapping("get_user")
    public ResponseEntity<?> getUser(@RequestParam(value = "userId") String user_id) {
        try {
            User user = userService.getUser(user_id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user가 존재하지 않습니다.");
            }
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("update_password")
    public ResponseEntity<?> updatePassword(
            @RequestParam(value = "userId") String user_id,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "newPassword") String new_password
    ) {
        try {
            userService.updatePassword(user_id, password, new_password);
            return ResponseEntity.ok("ok");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
