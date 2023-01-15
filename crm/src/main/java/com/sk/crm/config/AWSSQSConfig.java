package com.sk.crm.config;

import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AWSSQSConfig {

     @Value("${cloud.aws.region.static}")
     private String awsRegion;

     @Bean
     public QueueMessagingTemplate queueMessagingTemplate() {
          return new QueueMessagingTemplate(amazonSQSAsync());
     }

     @Bean
     @Primary
     public AmazonSQSAsync amazonSQSAsync() {
          return AmazonSQSAsyncClientBuilder
                  .standard()
                  .withRegion(awsRegion)
                  .build();
     }
}
