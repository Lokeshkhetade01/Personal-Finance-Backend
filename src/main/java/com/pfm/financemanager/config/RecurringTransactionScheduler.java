package com.pfm.financemanager.config;

import com.pfm.financemanager.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringTransactionScheduler {

    private final RecurringTransactionService recurringTransactionService;

    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyRecurringTransactionJob() {
        log.info("Running daily recurring transaction processing job");
        recurringTransactionService.processDueRecurringTransactions();
    }
}
