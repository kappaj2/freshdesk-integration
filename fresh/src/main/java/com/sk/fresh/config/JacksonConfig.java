package com.sk.fresh.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

     /**
      * Jackson Afterburner module to speed up serialization/deserialization.
      *
      * @return {@link AfterburnerModule} object
      */
     @Bean
     public AfterburnerModule afterburnerModule() {
          return new AfterburnerModule();
     }

     /**
      * ObjectMapper for JSON data.
      *
      * @return {@link ObjectMapper} Return a customer Jackson ObjectMapper instance.
      */
     @Bean(name = "objectMapper")
     @Qualifier("objectMapper")
     @Primary
     public ObjectMapper objectMapper() {
          return new ObjectMapper(new JsonFactory());
     }

}