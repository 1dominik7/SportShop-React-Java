package com.dominikdev.ecommerceshop.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
    private String fullName;
    private List<String> roleNames;
    private LocalDate dateOfBirth;
    private LocalDateTime createdDate;
}
