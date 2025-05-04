package com.dominikdev.ecommerceshop.discountCode;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("discount")
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;

    @PostMapping
    public ResponseEntity<DiscountCode> createDiscountCode(@RequestBody DiscountCodeRequest discountCodeRequest){
        DiscountCode discountCode = discountCodeService.createDiscountCode(discountCodeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(discountCode);
    }

    @PutMapping("{discountCodeId}")
    public DiscountCode updateDiscount(@PathVariable Integer discountCodeId, @RequestBody DiscountCodeRequest discountCodeRequest) {
        return discountCodeService.updateDiscountCode(discountCodeId, discountCodeRequest);
    }

    @GetMapping("{discountCodeId}")
    public DiscountCode getDiscountCodeById(@PathVariable Integer discountCodeId) {
        return discountCodeService.getDiscountCodeById(discountCodeId);
    }

    @GetMapping
    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeService.getAllDiscountCode();
    }

    @DeleteMapping("{discountCodeId}")
    public ResponseEntity<String> deleteDiscountCode(@PathVariable Integer discountCodeId){
        discountCodeService.deleteDiscountCode(discountCodeId);
        return ResponseEntity.ok("Discount code has been successfully deleted!");
    }

    @GetMapping("/active")
    public List<DiscountCode> getActiveDiscountCode() {
        return discountCodeService.getActiveDiscountCode();
    }
}
