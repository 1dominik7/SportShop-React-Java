package com.dominikdev.ecommerceshop.paymentType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("payment-type")
public class PaymentTypeController {

    private final PaymentTypeService paymentTypeService;

    @GetMapping("/all")
    public ResponseEntity<List<PaymentTypeResponse>> paymentTypeResponses() {
        List<PaymentTypeResponse> PaymentTypeResponses = paymentTypeService.getAllPaymentTypes();
        return ResponseEntity.status(HttpStatus.OK).body(PaymentTypeResponses);
    }
}
