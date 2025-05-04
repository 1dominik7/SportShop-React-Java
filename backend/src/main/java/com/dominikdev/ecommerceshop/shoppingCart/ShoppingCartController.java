package com.dominikdev.ecommerceshop.shoppingCart;

import com.dominikdev.ecommerceshop.exceptions.UnauthorizedException;
import com.dominikdev.ecommerceshop.user.User;
import com.dominikdev.ecommerceshop.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final AuthUtils authUtils;
    private final ShoppingCartRepository shoppingCartRepository;

    @PostMapping("/products/{productItemId}/quantity/{quantity}")
    public ResponseEntity<ShoppingCartResponse> addProductToCart(@PathVariable Integer productItemId, @PathVariable Integer quantity) {

        ShoppingCartResponse shoppingCart = shoppingCartService.addProductToCart(productItemId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingCart);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShoppingCartResponse>> getAllCarts() {
        List<ShoppingCartResponse> shoppingCartResponses = shoppingCartService.getAllCarts();
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartResponses);
    }

    @GetMapping("/users/cart")
    public ResponseEntity<ShoppingCartGetProdItemResponse> getCartById() {
        String email = authUtils.loggedInEmail();
        ShoppingCart shoppingCart = shoppingCartRepository.findCartByEmail(email);

        if (shoppingCart == null) {
            ShoppingCartGetProdItemResponse emptyResponse = new ShoppingCartGetProdItemResponse();
            emptyResponse.setId(null);
            emptyResponse.setShoppingCartItems(new ArrayList<>());
            return ResponseEntity.ok(emptyResponse);
        }

        Integer shoppingCartId = shoppingCart.getId();
        ShoppingCartGetProdItemResponse shoppingCartResponse = shoppingCartService.getCart(email, shoppingCartId);
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartResponse);
    }

    @PutMapping("/update/products/{productItemId}/quantity/{operation}")
    public ResponseEntity<ShoppingCartResponse> updateCartProduct(@PathVariable Integer productItemId, @PathVariable String operation) {
        ShoppingCartResponse shoppingCartResponse = shoppingCartService.updateProductQuantityInCart(productItemId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartResponse);
    }

    @DeleteMapping("/delete/{shoppingCartId}/product/{productItemId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Integer shoppingCartId, @PathVariable Integer productItemId) {
        String status = shoppingCartService.deleteProductFromCart(shoppingCartId, productItemId);

        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PostMapping("/add-discount/{shoppingCartId}")
    public ResponseEntity<Void> addDiscountToCart(@PathVariable Integer shoppingCartId, @RequestParam String discountCode) {
        shoppingCartService.addDiscountToCart(shoppingCartId, discountCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{shoppingCartId}")
    public ResponseEntity<String> deleteCartItemById(@PathVariable Integer shoppingCartId, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        System.out.println(shoppingCartId);
        String status = shoppingCartService.deleteCartById(shoppingCartId);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}
