package com.sk.fresh.service.freshdesk;

import com.sk.fresh.repository.FreshDeadLetterMessageRepository;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseMaintenanceService {

     @Value("${freshdesk.maintenance.db.days-to-keep}")
     private Integer numDaysToKeep;

     private final FreshReceivedMessageRepository freshReceivedMessageRepository;
     private final FreshDetailProcessingRepository freshDetailProcessingRepository;
     private final FreshDeadLetterMessageRepository freshDeadLetterMessageRepository;

     @Transactional(propagation = Propagation.REQUIRED)
     public void cleanOldMessageRecords() {
          var cal = Calendar.getInstance();
          cal.set(Calendar.HOUR_OF_DAY, 0);
          cal.set(Calendar.MINUTE, 0);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);

          cal.add(Calendar.DATE, numDaysToKeep * -1);
          var beforeDate = cal.getTime();

          log.info("Deleting before date : {}", beforeDate);
          freshDeadLetterMessageRepository.deleteByDateCreatedBefore(beforeDate);
          freshDetailProcessingRepository.deleteByDateCreatedBefore(beforeDate);
          freshReceivedMessageRepository.deleteByDateCreatedBefore(beforeDate);
     }
}
