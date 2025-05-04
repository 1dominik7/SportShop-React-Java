package com.dominikdev.ecommerceshop.address;

import com.dominikdev.ecommerceshop.user.User;
import com.dominikdev.ecommerceshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressResponse createAddress(AddressRequest addressRequest, Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));

    Address newAddress = Address.builder()
            .country(addressRequest.getCountry())
            .city(addressRequest.getCity())
            .firstName(addressRequest.getFirstName())
            .lastName(addressRequest.getLastName())
            .postalCode(addressRequest.getPostalCode())
            .street(addressRequest.getStreet())
            .phoneNumber(addressRequest.getPhoneNumber())
            .addressLine1(addressRequest.getAddressLine1())
            .addressLine2(addressRequest.getAddressLine2())
            .build();

    Address savedAddress = addressRepository.save(newAddress);

    user.getAddresses().add(savedAddress);
    userRepository.save(user);

    return AddressResponse.builder()
            .id(savedAddress.getId())
            .country(savedAddress.getCountry())
            .city(savedAddress.getCity())
            .firstName(savedAddress.getFirstName())
            .lastName(savedAddress.getLastName())
            .postalCode(savedAddress.getPostalCode())
            .street(savedAddress.getStreet())
            .phoneNumber(savedAddress.getPhoneNumber())
            .addressLine1(savedAddress.getAddressLine1())
            .addressLine2(savedAddress.getAddressLine2())
            .build();
    }

    public List<AddressResponse> getUserAddresses(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        List<Address> addresses = addressRepository.findAddressesByUserId(userId);

        List<AddressResponse> addressResponses = addresses.stream()
                .map(address -> {
                    AddressResponse response = new AddressResponse();
                    response.setId(address.getId());
                    response.setCountry(address.getCountry());
                    response.setCity(address.getCity());
                    response.setFirstName(address.getFirstName());
                    response.setLastName(address.getLastName());
                    response.setPostalCode(address.getPostalCode());
                    response.setStreet(address.getStreet());
                    response.setPhoneNumber(address.getPhoneNumber());
                    response.setAddressLine1(address.getAddressLine1());
                    response.setAddressLine2(address.getAddressLine2());
                    return response;
                })
                .collect(Collectors.toList());

        return addressResponses;
    }

    public AddressResponse getAddressById(Integer addressId, Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address with this id doesn't exists"));

        boolean exists = userRepository.existsByUserIdAndAddressId(userId, addressId);

        if(!exists){
            throw new RuntimeException("Address does not belong to this user");
        }

        AddressResponse addressResponse = new AddressResponse(
                address.getId(),
                address.getCountry(),
                address.getCity(),
                address.getFirstName(),
                address.getLastName(),
                address.getPostalCode(),
                address.getStreet(),
                address.getPhoneNumber(),
                address.getAddressLine1(),
                address.getAddressLine2()
        );

        return addressResponse;
    }

    public AddressResponse updateAddress(Integer addressId, AddressRequest addressRequest, Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        Address addressToUpdate = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address with this id doesn't exists"));

        if(!user.getAddresses().contains(addressToUpdate)){
            throw new RuntimeException("Address does not belong to this user");
        }

        addressToUpdate.setCountry(addressRequest.getCountry());
        addressToUpdate.setCity(addressRequest.getCity());
        addressToUpdate.setFirstName(addressRequest.getFirstName());
        addressToUpdate.setLastName(addressRequest.getLastName());
        addressToUpdate.setPostalCode(addressRequest.getPostalCode());
        addressToUpdate.setStreet(addressRequest.getStreet());
        addressToUpdate.setPhoneNumber(addressRequest.getPhoneNumber());
        addressToUpdate.setAddressLine1(addressRequest.getAddressLine1());
        addressToUpdate.setAddressLine2(addressRequest.getAddressLine2());

        Address updatedAddress = addressRepository.save(addressToUpdate);

        return AddressResponse.builder()
                .id(updatedAddress.getId())
                .country(updatedAddress.getCountry())
                .city(updatedAddress.getCity())
                .firstName(updatedAddress.getFirstName())
                .lastName(updatedAddress.getLastName())
                .postalCode(updatedAddress.getPostalCode())
                .street(updatedAddress.getStreet())
                .phoneNumber(updatedAddress.getPhoneNumber())
                .addressLine1(updatedAddress.getAddressLine1())
                .addressLine2(updatedAddress.getAddressLine2())
                .build();
    }

    public void deleteAddress(Integer addressId, Integer userId){
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        if(!user.getAddresses().contains(address)){
            throw new RuntimeException("Address does not belong to this user");
        }

        user.getAddresses().remove(address);
        userRepository.save(user);

        addressRepository.delete(address);
    }
}
