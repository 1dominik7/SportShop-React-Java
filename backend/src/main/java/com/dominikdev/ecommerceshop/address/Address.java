package com.dominikdev.ecommerceshop.address;

import com.dominikdev.ecommerceshop.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Postal code name cannot be blank")
    private String postalCode;

    @NotBlank(message = "Street name cannot be blank")
    private String street;

    @Pattern(regexp = "^[0-9]{9}$", message = "Phone number must be a 9-digit number")
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;

    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<User> users;
}
