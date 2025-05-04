package com.dominikdev.ecommerceshop.auth;

import com.dominikdev.ecommerceshop.user.UserResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private UserResponse user;
    private String token;
}
