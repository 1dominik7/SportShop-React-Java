package com.dominikdev.ecommerceshop.userPaymentMethod;

import com.dominikdev.ecommerceshop.paymentType.PaymentType;
import com.dominikdev.ecommerceshop.paymentType.PaymentTypeRepository;
import com.dominikdev.ecommerceshop.user.User;
import com.dominikdev.ecommerceshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPaymentMethodService {

    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final UserRepository userRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    public UserPaymentMethod createUserPaymentMethod(UserPaymentMethodRequest request) {

        if (request.getUserId() == null || request.getPaymentTypeId() == null) {
            throw new IllegalArgumentException("User ID and Payment Type ID are required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("Payment type not found with id: " + request.getPaymentTypeId()));

        UserPaymentMethod userPaymentMethod = UserPaymentMethod.builder()
                .user(user)
                .paymentType(paymentType)
                .provider(request.getProvider())
                .last4CardNumber(request.getLast4CardNumber())
                .paymentDate(request.getPaymentDate())
                .expiryDate(request.getExpiryDate())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault()) {
            setAsDefaultPaymentMethod(user, userPaymentMethod);
        }

        UserPaymentMethod savedPaymentMethod = userPaymentMethodRepository.save(userPaymentMethod);

        return savedPaymentMethod;
    }

    private void setAsDefaultPaymentMethod(User user, UserPaymentMethod newDefaultMethod) {
        List<UserPaymentMethod> existingMethods = user.getUserPaymentMethods();

        existingMethods.forEach(method -> method.setDefault(false));

        newDefaultMethod.setDefault(true);
    }

    public List<UserPaymentMethodResponse> getUserPaymentMethods (Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        List<UserPaymentMethod> userPaymentMethodResponses = userPaymentMethodRepository.findByUserId(userId);
        List<UserPaymentMethodResponse> userPaymentMethodResponseList = userPaymentMethodResponses.stream().map(
                payment -> {
                    UserPaymentMethodResponse userPaymentMethodResponse = new UserPaymentMethodResponse();
                    userPaymentMethodResponse.setId(payment.getId());
                    userPaymentMethodResponse.setUserId(payment.getUser().getId());
                    userPaymentMethodResponse.setPaymentTypeName(payment.getPaymentType().getValue());
                    userPaymentMethodResponse.setProvider(payment.getProvider());
                    userPaymentMethodResponse.setLast4CardNumber(payment.getLast4CardNumber());
                    userPaymentMethodResponse.setPaymentDate(payment.getPaymentDate());
                    userPaymentMethodResponse.setExpiryDate(payment.getExpiryDate());
                    userPaymentMethodResponse.setDefault(payment.isDefault());
                    userPaymentMethodResponse.setPayments(payment.getPayments());
                    return userPaymentMethodResponse;
                }).collect(Collectors.toList());

        return userPaymentMethodResponseList;
    }

}
