package com.dominikdev.ecommerceshop.discountCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCode createDiscountCode (DiscountCodeRequest discountCodeRequest){

        DiscountCode discountCode = new DiscountCode();
        discountCode.setName(discountCodeRequest.getName());
        discountCode.setCode(discountCodeRequest.getCode());
        discountCode.setExpiryDate(discountCodeRequest.getExpiryDate());
        discountCode.setDiscount(discountCodeRequest.getDiscount());

        return discountCodeRepository.save(discountCode);
    }

    public DiscountCode updateDiscountCode (Integer discountCodeId, DiscountCodeRequest discountCodeRequest){

        DiscountCode discountCode = discountCodeRepository.findById(discountCodeId).orElseThrow(() -> new RuntimeException("Discount code with this id does not exist!"));

        discountCode.setName(discountCodeRequest.getName());
        discountCode.setCode(discountCodeRequest.getCode());
        discountCode.setExpiryDate(discountCodeRequest.getExpiryDate());
        discountCode.setDiscount(discountCodeRequest.getDiscount());

        return discountCodeRepository.save(discountCode);
    }

    public DiscountCode getDiscountCodeById(Integer discountCodeId){
        DiscountCode discountCode = discountCodeRepository.findById(discountCodeId).orElseThrow(() -> new RuntimeException("Discount code with this id does not exist!"));

        return discountCode;
    }

    public List<DiscountCode> getAllDiscountCode(){
        List<DiscountCode> discountCodes = discountCodeRepository.findAll();

        return discountCodes;
    }

    public void deleteDiscountCode(Integer discountCodeId){
        DiscountCode discountCode = discountCodeRepository.findById(discountCodeId).orElseThrow(() -> new RuntimeException("Discount code not found"));
        discountCodeRepository.delete(discountCode);
    }

    public List<DiscountCode> getActiveDiscountCode(){

        LocalDateTime now = LocalDateTime.now();

        return discountCodeRepository.findByExpiryDateGreaterThanEqual(now);
    }
}
