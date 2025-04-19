package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
@Component
public class TransactionHandler {

//    private TransactionRepository transactionRepository;
//
//    public Mono<ServerResponse> processTransaction(ServerRequest request) {
//        return request.bodyToMono(TransactionRequest.class)
//                .flatMap(service::handleTransaction)
//                .flatMap(response -> ServerResponse.ok().bodyValue(response))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
//    }
}
