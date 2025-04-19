package com.nova.bank.novabank.security;// TODO: Implement CustomUserDetailsService.java



import com.nova.bank.novabank.repository.CustomerRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return customerRepository.findByEmail(username)
                .map(customer -> User.builder()
                        .username(customer.getEmail())
                        .password(customer.getPassword()) // must be encoded
                        .roles("USER") // or load from customer.getRole()
                        .build());
    }
}
