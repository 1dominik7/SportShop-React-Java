package com.dominikdev.ecommerceshop.userPaymentMethod;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user-payment-method")
public class UserPaymentMethodController {

    final private UserPaymentMethodService userPaymentMethodService;

    @PostMapping
    public ResponseEntity<UserPaymentMethod> createUserPaymentMethod(UserPaymentMethodRequest request){
        UserPaymentMethod userPaymentMethod = userPaymentMethodService.createUserPaymentMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPaymentMethod);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserPaymentMethodResponse>> getUserPaymentMethods(@PathVariable Integer userId){
        List<UserPaymentMethodResponse> userPaymentMethodResponses = userPaymentMethodService.getUserPaymentMethods(userId);
        return ResponseEntity.ok(userPaymentMethodResponses);
    }
}
