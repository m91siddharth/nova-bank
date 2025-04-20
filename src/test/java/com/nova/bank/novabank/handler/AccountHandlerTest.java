package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.model.Account;
import com.nova.bank.novabank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class AccountHandlerTest {
    private static final long VALID_ACCOUNT_ID = 12345L;
    private static final String CUSTOMER_NAME = "John Doe";
    private static final double ACCOUNT_BALANCE = 5000.0;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountHandler accountHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountById_ValidAccountId_ReturnsAccount() {
        // Arrange
        Account account = createTestAccount(VALID_ACCOUNT_ID, CUSTOMER_NAME, ACCOUNT_BALANCE);
        when(accountRepository.findById(VALID_ACCOUNT_ID)).thenReturn(Mono.just(account));
        ServerRequest serverRequest = mockServerRequestWithAccountId(String.valueOf(VALID_ACCOUNT_ID));

        // Act
        Mono<ServerResponse> response = accountHandler.getAccountById(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
        verify(accountRepository).findById(VALID_ACCOUNT_ID);
    }

    @Test
    void getAccountById_InvalidAccountId_ReturnsNotFound() {
        // Arrange
        when(accountRepository.findById(VALID_ACCOUNT_ID)).thenReturn(Mono.empty());
        ServerRequest serverRequest = mockServerRequestWithAccountId(String.valueOf(VALID_ACCOUNT_ID));

        // Act
        Mono<ServerResponse> response = accountHandler.getAccountById(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
        verify(accountRepository).findById(VALID_ACCOUNT_ID);
    }

    @Test
    void createAccount_ValidAccountDetails_ReturnsSuccess() {
        // Arrange
        Account accountToSave = createTestAccount(null, CUSTOMER_NAME, ACCOUNT_BALANCE);
        Account savedAccount = createTestAccount(VALID_ACCOUNT_ID, CUSTOMER_NAME, ACCOUNT_BALANCE);
        when(accountRepository.save(accountToSave)).thenReturn(Mono.just(savedAccount));
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(Account.class)).thenReturn(Mono.just(accountToSave));

        // Act
        Mono<ServerResponse> response = accountHandler.createAccount(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful()
                        && savedAccount.getCustomerId().equals(CUSTOMER_NAME)
                        && savedAccount.getBalance() == ACCOUNT_BALANCE)
                .verifyComplete();
        verify(accountRepository).save(accountToSave);
    }


    private ServerRequest mockServerRequestWithAccountId(String accountId) {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable("accountId")).thenReturn(accountId); // Updated path variable name
        return serverRequest;
    }

    private Account createTestAccount(Long id, String customerId, double balance) {
        Account account = new Account();
        account.setId(id);
        account.setCustomerId(customerId);
        account.setBalance(balance);
        account.setAccountNumber(id != null ? id.toString() : null); // Optional accountNumber assignment
        return account;
    }
}