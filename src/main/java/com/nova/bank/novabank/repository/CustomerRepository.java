package com.nova.bank.novabank.repository;// TODO: Implement CustomerRepository.java



import com.nova.bank.novabank.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Mono<Customer> findByEmail(String email);
}
