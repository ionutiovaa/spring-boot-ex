package com.iova.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age,
        String country
) {
}
