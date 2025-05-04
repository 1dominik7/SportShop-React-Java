package com.dominikdev.ecommerceshop.user;

import com.dominikdev.ecommerceshop.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Optional<User>> getUserProfile(@RequestHeader("Authorization") String jwt){
        try {
            Optional<User> user = userService.findUserByJwtToken(jwt);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody UserUpdateRequest request, @AuthenticationPrincipal User user){
        if (user == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        User updatedUser = userService.updateUserProfile(user.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }
}

