package com.dominikdev.ecommerceshop.address;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Integer id;
    private String country;
    private String city;
    private String firstName;
    private String lastName;
    private String postalCode;
    private String street;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
}
