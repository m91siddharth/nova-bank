package com.nova.bank.novabank.security;// TODO: Implement JwtAuthenticationFilter.java

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public ServerSecurityContextRepository getSecurityContextRepository() {
        return securityContextRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        logger.debug("Attempting to extract Authorization header.");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header found and begins with Bearer.");

            String token = authHeader.substring(7);
            logger.debug("Extracted token: {}", token);

            if (jwtUtil.validateToken(token)) {
                logger.debug("JWT token is valid.");

                String username = jwtUtil.getUsernameFromToken(token);
                logger.debug("Extracted username from token: {}", username);

                return userDetailsService.findByUsername(username)
                        .doOnNext(userDetails -> logger.debug("Found userDetails for username: {}", username))
                        .doOnError(error -> logger.error("Error occurred while loading userDetails for username: {}", username, error))
                        .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
                        .map(auth -> new SecurityContextImpl(auth))
                        .flatMap(securityContext -> securityContextRepository.save(exchange, securityContext))
                        .then(chain.filter(exchange))
                        .doOnError(error -> logger.error("Error occurred during authentication filter chain: {}", error.getMessage(), error));
            }
        } else {
            logger.warn("Authorization header is missing or does not start with Bearer.");
        }

        return chain.filter(exchange)
                .doOnSuccess(unused -> logger.debug("Successfully processed request in filter chain."))
                .doOnError(error -> logger.error("Error occurred while processing request in filter chain: {}", error.getMessage(), error));
    }
}
