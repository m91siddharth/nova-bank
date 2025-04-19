package com.nova.bank.novabank.repository;

import com.nova.bank.novabank.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Account> findById(Long Id); // for customer-owned accounts
}