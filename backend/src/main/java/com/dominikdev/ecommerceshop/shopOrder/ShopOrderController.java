package com.dominikdev.ecommerceshop.shopOrder;

import com.dominikdev.ecommerceshop.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("shop-order")
public class ShopOrderController {

    private final ShopOrderService shopOrderService;

    @PostMapping
    public ResponseEntity<ShopOrder> addProductToCart(@RequestBody ShopOrderRequest request, @RequestHeader("Authorization") String jwt) {

        ShopOrder shopOrder = shopOrderService.createShopOrder(request);
        return ResponseEntity.ok(shopOrder);
    }

    @PostMapping("/create")
    public ResponseEntity<ShopOrder> createOrder(@RequestBody ShopOrderRequest request) {

        ShopOrder order = shopOrderService.createShopOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user")
    public  ResponseEntity<List<ShopOrderResponse>> getAllUserShopOrder(@AuthenticationPrincipal User user){

        List<ShopOrderResponse> shopOrders = shopOrderService.getUserShopOrders(user.getId());
        return ResponseEntity.ok(shopOrders);
    }

    @GetMapping("/user/{shopOrderId}")
    public ResponseEntity<ShopOrderResponse> getUserShopOrderById(@PathVariable Integer shopOrderId, @AuthenticationPrincipal User user){
        ShopOrderResponse ShopOrder = shopOrderService.getUserShopOrderById(shopOrderId, user.getId());
        return ResponseEntity.ok(ShopOrder);
    }
}
