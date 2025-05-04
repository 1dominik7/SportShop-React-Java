package com.dominikdev.ecommerceshop.shippingMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethodResponse {

    public Integer id;
    public String name;
    public double price;
}
