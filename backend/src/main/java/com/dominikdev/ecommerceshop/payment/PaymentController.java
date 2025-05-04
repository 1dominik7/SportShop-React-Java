package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrderService;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethodService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final ShopOrderService shopOrderService;
    private final StripePaymentService stripePaymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(PaymentRequest paymentRequest) throws InsufficientResourcesException {
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    @PostMapping("/stripe/checkout")
    public ResponseEntity<?> createStripeCheckoutSession(@RequestBody StripeCheckoutRequest request) {
        try {
            ShopOrder order = shopOrderService.createShopOrder(request.getOrderRequest());

            String checkoutUrl = stripePaymentService.createCheckoutSession(
                    order,
                    request.getSuccessUrl(),
                    request.getCancelUrl()
            );

            return ResponseEntity.ok(Map.of(
                    "checkoutUrl", checkoutUrl,
                    "orderId", order.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            stripePaymentService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid signature");

        } catch (StripeException e) {
            log.error("Stripe processing error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Stripe error: " + e.getMessage());

        } catch (NumberFormatException e) {
            log.error("Invalid metadata format", e);
            return ResponseEntity.badRequest()
                    .body("Invalid order_id or user_id format");

        } catch (Exception e) {
            log.error("Unexpected error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }

    @GetMapping("/verify/{paymentIntentId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String paymentIntentId) {
        try {
            PaymentVerificationResponse response = paymentService.verifyPayment(paymentIntentId);

            if (response.getStatus() == Payment.PaymentStatus.SUCCEEDED) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during verify payment: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
