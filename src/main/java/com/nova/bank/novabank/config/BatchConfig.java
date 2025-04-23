package com.nova.bank.novabank.config;
import com.nova.bank.novabank.handler.AccountHandler;
import com.nova.bank.novabank.handler.InterestHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class BatchConfig implements SchedulingConfigurer {

    private final InterestHandler interestHandler;

    public BatchConfig(InterestHandler interestHandler) {
        this.interestHandler = interestHandler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        CronTask cronTask = new CronTask(() -> {
            try {
                interestHandler.calculateInterest();
            } catch (Exception e) {
                System.err.println("Batch job execution failed: " + e.getMessage());
                e.printStackTrace();
            }
        }, "*/120 * * * * *");
        taskRegistrar.addCronTask(cronTask);
    }
}