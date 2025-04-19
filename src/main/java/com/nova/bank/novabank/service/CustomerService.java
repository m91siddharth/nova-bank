package com.nova.bank.novabank.service;// TODO: Implement CustomerService.java



import com.nova.bank.novabank.model.Customer;
import com.nova.bank.novabank.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Object> registerCustomer(Customer customer) {
        return customerRepository.findByEmail(customer.getEmail())
                .flatMap(existing -> Mono.error(new RuntimeException("Email already registered")))
                .switchIfEmpty(Mono.defer(() -> {
                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                    return customerRepository.save(customer);
                }));
    }
}
