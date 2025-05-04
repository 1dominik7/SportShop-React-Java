package com.dominikdev.ecommerceshop.user;

import com.dominikdev.ecommerceshop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Optional<User> findUserByJwtToken(String jwt) {
        String jwtToken = jwt.substring(7);

        String email = jwtService.getEmailFromJwtToken(jwtToken);

        return findUserByEmail(email);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserProfile(Integer userId, UserUpdateRequest request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDateOfBirth(request.getDateOfBirth());

        return userRepository.save(user);
    }
}
