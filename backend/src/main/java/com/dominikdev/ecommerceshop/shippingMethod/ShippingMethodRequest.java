package com.dominikdev.ecommerceshop.shippingMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethodRequest {
   public String name;
   public Double price;
}
