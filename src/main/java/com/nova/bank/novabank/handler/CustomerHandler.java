package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.model.Customer;
import com.nova.bank.novabank.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@Slf4j // Lombok annotation to inject the SLF4J logger
public class CustomerHandler {

    private final CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private final jakarta.validation.Validator validator;

    @Autowired
    public CustomerHandler(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, jakarta.validation.Validator validator) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public Mono<ServerResponse> registerCustomer(@Valid ServerRequest request) {
        log.info("Registering a new customer"); // Example of logging

        return request.bodyToMono(Customer.class)
                .flatMap(customer -> {
                    var violations = validator.validate(customer);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));

                        log.warn("Validation failed: {}", errorMessage); // Example of warning logging
                        return ServerResponse.badRequest().bodyValue("Validation failed: " + errorMessage);
                    }

                    log.debug("Hashing the customer's password"); // Debug-level logging
                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));

                    return customerRepository.save(customer)
                            .flatMap(saved -> {
                                log.info("Successfully registered customer with ID: {}", saved.getId());
                                return ServerResponse.ok().bodyValue(saved);
                            });
                });
    }

    public Mono<ServerResponse> getAllCustomers(ServerRequest request) {
        log.info("Fetching all customers");
        return customerRepository.findAll()
                .collectList()
                .flatMap(customers -> {
                    log.debug("Found {} customers in the database", customers.size());
                    return ServerResponse.ok().bodyValue(customers);
                });
    }
}