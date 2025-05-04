package com.dominikdev.ecommerceshop.shippingMethod;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    public ShippingMethod addShippingMethod(ShippingMethodRequest shippingMethodRequest) {

        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setName(shippingMethodRequest.name);
        shippingMethod.setPrice(shippingMethodRequest.price);

        return shippingMethodRepository.save(shippingMethod);
    }

    public List<ShippingMethodResponse> getAllShippingMethod() {
        return shippingMethodRepository.findAll().stream().map(shippingMethod -> ShippingMethodResponse.builder()
                .id(shippingMethod.getId())
                .name(shippingMethod.getName())
                .price(shippingMethod.getPrice()
                ).build()).collect(Collectors.toList());
    }
}
