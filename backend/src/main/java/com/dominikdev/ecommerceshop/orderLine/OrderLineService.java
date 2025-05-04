package com.dominikdev.ecommerceshop.orderLine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository orderLineRepository;

    public List<OrderLine> getOrderLinesByIds(List<Integer> orderLineIds){
        return orderLineRepository.findAllById(orderLineIds);
    }

}
