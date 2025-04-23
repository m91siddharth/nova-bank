package com.nova.bank.novabank.handler;

import com.nova.bank.novabank.repository.InterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class InterestHandler implements InterestRepository {

    @Autowired
    private AccountHandler accountHandler;

    public void calculateInterest() {
        log.info("Starting interest calculation for all accounts...");

        var allAccounts = accountHandler.getAllAccounts();

        BigDecimal interestRatePerMinute = new BigDecimal("0.0001");

        var updatedAccounts = allAccounts.toStream()
                .peek(account -> {
                    LocalDateTime lastUpdated = account.getLastUpdated();
                    LocalDateTime now = LocalDateTime.now();
                    if (lastUpdated == null) {
                        log.warn("Account has a null lastUpdated timestamp and will be skipped.", account.getId());
                        return;
                    }
                    long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdated, now);

                    if (minutesElapsed > 0) {
                        log.info("Calculating interest for account ID: {}", account.getId());
                        BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());
                        BigDecimal interest = currentBalance.multiply(interestRatePerMinute)
                                .multiply(BigDecimal.valueOf(minutesElapsed))
                                .setScale(2, RoundingMode.HALF_UP);

                        BigDecimal newBalance = currentBalance.add(interest);
                        account.setBalance(newBalance.doubleValue());

                        account.setLastUpdated(now);

                        log.info("Updated account ID: {} with new balance: {}", account.getId(), newBalance);
                    } else {
                        log.info("No interest calculated for account ID: {} (no time elapsed)", account.getId());
                    }
                })
                .toList();
        accountHandler.updateAccounts(updatedAccounts);
        log.info("Interest calculation for all accounts completed.");
    }
}
