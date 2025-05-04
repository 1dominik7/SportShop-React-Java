package com.dominikdev.ecommerceshop.address;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("address")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest, @PathVariable Integer userId){
        AddressResponse address = addressService.createAddress(addressRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Integer userId){
        List<AddressResponse> addressResponseList = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addressResponseList);
    }

    @GetMapping("byId/{addressId}")
    public ResponseEntity<AddressResponse> getUserAddresses(@PathVariable Integer addressId, @RequestParam Integer userId){
        AddressResponse addressResponseList = addressService.getAddressById(addressId,userId);
        return ResponseEntity.ok(addressResponseList);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Integer addressId, @RequestBody AddressRequest addressRequest, @RequestParam Integer userId){
        AddressResponse address = addressService.updateAddress(addressId, addressRequest, userId);
        return ResponseEntity.status(HttpStatus.OK).body(address);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer addressId, @RequestParam Integer userId){
        addressService.deleteAddress(addressId,userId);
        return ResponseEntity.ok("Address has been successfully deleted!");
    }
}
