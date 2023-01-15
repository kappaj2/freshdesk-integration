package com.sk.fresh;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.sk.fresh.service.crm.CRMManagementService;

import java.util.Optional;

@TestConfiguration
public class SetupTestConfiguration {

     @Primary
     @Bean
     public CRMManagementService getCRMManagementService() {
          CRMManagementService service = alpha2Code -> Optional.empty();
          return service;
     }
}
