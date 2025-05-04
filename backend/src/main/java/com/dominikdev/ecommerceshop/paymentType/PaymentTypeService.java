package com.dominikdev.ecommerceshop.paymentType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;


    public List<PaymentTypeResponse> getAllPaymentTypes(){
        return paymentTypeRepository.findAll().stream().map(paymentType -> PaymentTypeResponse.builder()
                .id(paymentType.getId())
                .value(paymentType.getValue()).build()).collect(Collectors.toList());
    }
}
