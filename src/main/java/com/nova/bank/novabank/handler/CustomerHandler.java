package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.exception.CustomerValidationException;
import com.nova.bank.novabank.model.Customer;
import com.nova.bank.novabank.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class CustomerHandler {
    private static final Logger log = LogManager.getLogger(CustomerHandler.class);


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
        log.info("Registering customer");
        return request.bodyToMono(Customer.class)
                .flatMap(customer -> {
                    var violations = validator.validate(customer);
                    if (!violations.isEmpty()) {
                        String message = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));
                        throw new CustomerValidationException("Validation failed", message);
                    }

                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                    return customerRepository.save(customer)
                            .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
                });
    }

    public Mono<ServerResponse> getAllCustomers(ServerRequest request) {
        return customerRepository.findAll()
                .collectList()
                .flatMap(customers -> ServerResponse.ok().bodyValue(customers));
    }
}
