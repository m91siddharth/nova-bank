package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class StatementHandler {

    private final TransactionRepository transactionRepository;

    public StatementHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Mono<ServerResponse> getStatements(ServerRequest request) {
        log.info("Getting statements");
        String accountId = request.pathVariable("accountId");
        return transactionRepository.findByAccountId(accountId)
                .collectList()
                .flatMap(transactions -> ServerResponse.ok().bodyValue(transactions));
    }
}
