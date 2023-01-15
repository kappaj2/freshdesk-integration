package com.sk.fresh.schdules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.sk.fresh.service.freshdesk.DatabaseMaintenanceService;

@RequiredArgsConstructor
@Component
@Slf4j
public class FreshDeskTaskSchedules {

     private final DatabaseMaintenanceService databaseMaintenanceService;

     /**
      * Refer document on for more detail on parameters for the schedule expression.
      * https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
      * "0 0 02 * * ?"
      * seconds     => 0
      * minutes     => 0
      * hours       => every morning at 02h00
      * day         => every day, month and year
      */
     @Scheduled(cron = "0 0 02 * * ?")
     public void removeOldDatabaseRecordsAlreadyProcessed() {
          databaseMaintenanceService.cleanOldMessageRecords();
     }
}
