package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.model.Customer;
import com.nova.bank.novabank.repository.CustomerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

class CustomerHandlerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @InjectMocks
    private CustomerHandler customerHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCustomer_ValidCustomer_ReturnsOkResponse() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");

        when(validator.validate(customer)).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("hashed_password");
        when(customerRepository.save(customer)).thenReturn(Mono.just(customer));

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(Customer.class)).thenReturn(Mono.just(customer));

        // Act
        Mono<ServerResponse> response = customerHandler.registerCustomer(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(validator).validate(customer);
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(customer);
    }

    @Test
    void registerCustomer_InvalidCustomer_ReturnsBadRequest() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName(""); // Invalid name

        Set<ConstraintViolation<Customer>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(customer)).thenReturn(violations);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(Customer.class)).thenReturn(Mono.just(customer));

        // Act
        Mono<ServerResponse> response = customerHandler.registerCustomer(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();

        verify(validator).validate(customer);
        verifyNoInteractions(passwordEncoder, customerRepository); // No further processing since validation failed
    }

    @Test
    void registerCustomer_RepositoryError_ReturnsServerError() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");

        when(validator.validate(customer)).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("hashed_password");
        when(customerRepository.save(customer)).thenReturn(Mono.error(new RuntimeException("Database error")));

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(Customer.class)).thenReturn(Mono.just(customer));

        // Act
        Mono<ServerResponse> response = customerHandler.registerCustomer(serverRequest);

        // Assert
        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Database error"))
                .verify();

        verify(validator).validate(customer);
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(customer);
    }
}
