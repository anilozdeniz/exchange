package com.marcura.exchange.tasks;

import com.marcura.exchange.service.FetchCurrencyRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    @Autowired
    private FetchCurrencyRatesService fetchCurrencyRatesService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a");

    // Start cron job every day at 12:05 AM GMT
    @Scheduled(cron ="0 5 0 * * ? ",zone = "GMT")
    public void scheduleTaskWithCron() {
        String currentDateTime = dateTimeFormatter.format(LocalDateTime.now());
        logger.info("ScheduleTaskWithCron :: Execution Time - {}", currentDateTime);
        try {
            // Update or insert results from api to internal database
            fetchCurrencyRatesService.retrieveRates();
            logger.info("ScheduleTaskWithCron :: Saved currency rates");
        } catch (Exception e) {
            logger.error("ScheduleTaskWithCron :: Error - {}",e.getMessage(),e);
        }
    }
}
