package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.paymentType.PaymentType;
import com.dominikdev.ecommerceshop.paymentType.PaymentTypeRepository;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethod;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethodRepository;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethodRequest;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethodService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentService {

    private final PaymentTypeRepository paymentTypeRepository;
    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final UserPaymentMethodService userPaymentMethodService;
    private final PaymentService paymentService;

    @Value("${application.stripe.webhook-secret}")
    private String stripeWebhookSecret;

    public String createCheckoutSession(ShopOrder order, String successUrl, String cancelUrl)
            throws StripeException {

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "&payment_intent_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl + "&payment_intent_id={CHECKOUT_SESSION_ID}")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (order.getFinalOrderTotal() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Order #" + order.getId())
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("order_id", order.getId().toString())
                .putMetadata("user_id", order.getUserId().toString())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) throws StripeException, InsufficientResourcesException {

        String webhookSecret = stripeWebhookSecret;

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
                handleSuccessfulPayment(session);
            }
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature! Check webhook secret");
            throw e;
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            throw e;
        }
    }

    private void handleSuccessfulPayment(Session session) throws StripeException, InsufficientResourcesException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());

        Integer orderId = Integer.parseInt(session.getMetadata().get("order_id"));
        Integer userId = Integer.parseInt(session.getMetadata().get("user_id"));

        int expMonth = paymentMethod.getCard().getExpMonth().intValue();
        int expYear = paymentMethod.getCard().getExpYear().intValue();

        LocalDate expiryDate = YearMonth.of(expYear, expMonth).atEndOfMonth();
        LocalDateTime expiryDateTime = expiryDate.atStartOfDay();

        String cardBrand = paymentMethod.getCard().getBrand();
        String last4 = paymentMethod.getCard().getLast4();

        Optional<UserPaymentMethod> existingPaymentMethod = userPaymentMethodRepository.findByUserIdAndLast4CardNumber(userId, last4);

        UserPaymentMethod userPaymentMethod;

        if (existingPaymentMethod.isPresent()) {
            userPaymentMethod = existingPaymentMethod.get();
            userPaymentMethod.setExpiryDate(expiryDateTime);
            userPaymentMethodRepository.save(userPaymentMethod);
        } else {
            PaymentType paymentType = paymentTypeRepository.findByValue(cardBrand)
                    .orElseGet(() -> {
                        PaymentType newType = new PaymentType();
                        newType.setValue(cardBrand);
                        return paymentTypeRepository.save(newType);
                    });

            UserPaymentMethodRequest paymentMethodRequest = new UserPaymentMethodRequest();
            paymentMethodRequest.setUserId(userId);
            paymentMethodRequest.setPaymentTypeId(paymentType.getId());
            paymentMethodRequest.setProvider("Stripe");
            paymentMethodRequest.setLast4CardNumber(last4);
            paymentMethodRequest.setPaymentDate(LocalDateTime.now());
            paymentMethodRequest.setExpiryDate(expiryDateTime);
            paymentMethodRequest.setDefault(false);

            userPaymentMethod = userPaymentMethodService.createUserPaymentMethod(paymentMethodRequest);

        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setShopOrderId(orderId);
        paymentRequest.setUserPaymentMethodId(userPaymentMethod.getId());
        paymentRequest.setTransactionId(paymentIntent.getId());
        paymentRequest.setPaymentIntentId(session.getId());
        paymentRequest.setStatus(Payment.PaymentStatus.SUCCEEDED);

        paymentService.createPayment(paymentRequest);
    }
}
