package com.sk.fresh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.sk.fresh.config.AWSProperties;
import com.sk.fresh.config.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class, AWSProperties.class})
@Slf4j
@EnableSqs
@EnableScheduling
public class FreshApplication {

     public static void main(String[] args) {
          SpringApplication.run(FreshApplication.class, args);
     }

}
