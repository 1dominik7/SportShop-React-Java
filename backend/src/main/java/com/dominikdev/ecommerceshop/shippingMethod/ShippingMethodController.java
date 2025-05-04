package com.dominikdev.ecommerceshop.shippingMethod;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("shipping-method")
public class ShippingMethodController {

    private final ShippingMethodService shippingMethodService;

    @PostMapping()
    public ResponseEntity<ShippingMethod> addShippingMethod(@RequestBody ShippingMethodRequest shippingMethodRequest){

        ShippingMethod shippingMethod = shippingMethodService.addShippingMethod(shippingMethodRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(shippingMethod);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShippingMethodResponse>> getAllShippingMethod(){
        List<ShippingMethodResponse> shippingMethods = shippingMethodService.getAllShippingMethod();
        return ResponseEntity.status(HttpStatus.OK).body(shippingMethods);
    }
}
