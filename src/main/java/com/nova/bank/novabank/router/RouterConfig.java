package com.nova.bank.novabank.router;// TODO: Implement RouterConfig.java


import com.nova.bank.novabank.handler.AccountHandler;
import com.nova.bank.novabank.handler.CustomerHandler;
import com.nova.bank.novabank.handler.StatementHandler;
import com.nova.bank.novabank.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(
            CustomerHandler customerHandler,
            AccountHandler accountHandler,
            TransactionHandler transactionHandler,
            StatementHandler statementHandler
    ) {
        return RouterFunctions.route()
                // Customer Routes
                .POST("/api/customers", customerHandler::registerCustomer)
                .GET("/api/view/customers", customerHandler::getAllCustomers)

                // Account Routes
                .POST("/api/accounts", accountHandler::createAccount)
                .GET("/api/accounts/{accountId}", accountHandler::getAccountById)

              // Statement Routes
                .GET("/api/statements/{accountId}", statementHandler::getStatements)

                .build();
        // Transaction Routes
//                .POST("/api/transactions", transactionHandler::processTransaction)


    }
    /*
        POST /api/customers: Register a new customer.
        GET /api/accounts/{accountId}: Get account details.
        POST /api/accounts: Create a new account for a customer.
        POST /api/transactions: Perform transactions (deposit, withdrawal, transfer).
        GET /api/statements/{accountId}: Retrieve account statements.
        GET /api/view/customers :  see all customers

     */
}
