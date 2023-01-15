package com.sk.fresh.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

     public final RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

     public RestTemplateConfig getRestTemplateConfig() {
          return restTemplateConfig;
     }

     @Data
     public static class RestTemplateConfig {

          private Integer connectTimeout;
          private Integer readTimeout;
          private String apiToken;
          private String password;
     }
}