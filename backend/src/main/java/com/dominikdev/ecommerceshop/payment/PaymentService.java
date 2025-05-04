package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.product.productItem.ProductItemRepository;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrderRepository;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethod;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethodRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${application.stripe.secret-key}")
    private String stripeSecretKey;

    private final PaymentRepository paymentRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) throws InsufficientResourcesException {

        if (request.getShopOrderId() == null || request.getUserPaymentMethodId() == null) {
            throw new IllegalArgumentException("Shop order and Payment Method ID are required");
        }

        ShopOrder shopOrder = shopOrderRepository.findById(request.getShopOrderId())
                .orElseThrow(() -> new RuntimeException("Shop order not found with id: " + request.getShopOrderId()));

        if (request.getStatus() != Payment.PaymentStatus.SUCCEEDED) {
            throw new RuntimeException("Payment not completed successfully. Current status: " + request.getStatus());
        }

        updateProductStock(shopOrder);

        UserPaymentMethod userPaymentMethod = userPaymentMethodRepository.findById(request.getUserPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("User payment method not found with id: " + request.getUserPaymentMethodId()));

        Payment payment = Payment.builder()
                .transactionId(request.getTransactionId())
                .paymentIntentId(request.getPaymentIntentId())
                .shopOrder(shopOrder)
                .paymentMethod(userPaymentMethod)
                .status(request.getStatus())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .id(savedPayment.getId())
                .transactionId(savedPayment.getTransactionId())
                .paymentIntentId(savedPayment.getPaymentIntentId())
                .paymentMethod(savedPayment.getPaymentMethod())
                .createdAt(savedPayment.getCreatedAt())
                .updatedAt(savedPayment.getUpdatedAt())
                .status(savedPayment.getStatus())
                .build();
    }

    public PaymentVerificationResponse verifyPayment(String paymentIntentId) throws StripeException{
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentIntent intent = PaymentIntent.retrieve(payment.getTransactionId());

        if("succeeded".equals(intent.getStatus())){
            ShopOrder shopOrder = shopOrderRepository.findByPaymentPaymentIntentId(paymentIntentId);
            if (shopOrder != null) {
                UserPaymentMethod paymentMethod = userPaymentMethodRepository.findById(shopOrder.getPayment().getPaymentMethod().getId()).orElse(null);

                return PaymentVerificationResponse.builder()
                        .id(shopOrder.getPayment().getId())
                        .transactionId(intent.getId())
                        .paymentIntentId(paymentIntentId)
                        .shopOrder(shopOrder)
                        .paymentMethod(paymentMethod)
                        .createdAt(shopOrder.getPayment().getCreatedAt())
                        .updatedAt(shopOrder.getPayment().getUpdatedAt())
                        .status(shopOrder.getPayment().getStatus())
                        .build();
            } else {
                return PaymentVerificationResponse.builder()
                        .status(Payment.PaymentStatus.FAILED)
                        .build();
            }
        } else {
            return PaymentVerificationResponse.builder()
                    .status(Payment.PaymentStatus.FAILED)
                    .build();

        }
    }

    private void updateProductStock(ShopOrder order) throws InsufficientResourcesException {
        for(OrderLine orderLine: order.getOrderLines()){
            ProductItem productItem = orderLine.getProductItem();
            int orderedQty = orderLine.getQty();

            if(productItem.getQtyInStock() < orderedQty){
                throw new InsufficientResourcesException("Not enough stock for product: " + productItem.getId());
            }

            productItem.setQtyInStock(productItem.getQtyInStock() - orderedQty);
            productItemRepository.save(productItem);
        }
    }
}
