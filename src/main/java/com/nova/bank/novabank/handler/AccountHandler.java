package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.model.Account;
import com.nova.bank.novabank.repository.AccountRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AccountHandler {

    private final AccountRepository accountRepository;
    @Autowired
    public AccountHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Mono<ServerResponse> createAccount(@Valid ServerRequest request) {
        log.info("Creating a new account");
        return request.bodyToMono(Account.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is empty")))
                .flatMap(accountRepository::save)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED).bodyValue(saved));
    }

    public Mono<ServerResponse> getAccountById(ServerRequest request) {
        log.info("Getting account by ID");
        long accountId = Long.parseLong(request.pathVariable("accountId"));
        log.info("Account ID: {}", accountId);
        return accountRepository.findById(accountId)
                .flatMap(account -> ServerResponse.ok().bodyValue(account))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Flux<Account> getAllAccounts() {
        log.info("Getting all accounts ");
        return accountRepository.findAll();
    }

    public void updateAccounts(List<Account> updatedAccounts) {
        log.info("Updating accounts");
        accountRepository.saveAll(updatedAccounts);
    }
}
