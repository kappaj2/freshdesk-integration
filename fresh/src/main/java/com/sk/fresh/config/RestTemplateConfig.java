package com.sk.fresh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

     @Autowired
     private ApplicationProperties props;

     @Bean
     public RestTemplate restTemplate(RestTemplateBuilder builder) {

          List<ClientHttpRequestInterceptor> addInterceptors = new ArrayList<>();
          var restTemplate = builder.basicAuthentication(props.getRestTemplateConfig().getApiToken(),
                  props.getRestTemplateConfig().getPassword()).build();
          restTemplate.getInterceptors().addAll(addInterceptors);
          return restTemplate;
     }

     @Bean
     public RestTemplateCustomizer restTemplateCustomizer() {
          return restTemplate -> {
               restTemplate.setRequestFactory(clientHttpRequestFactory());
          };
     }

     @Bean
     public ClientHttpRequestFactory clientHttpRequestFactory() {
          var clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
          clientHttpRequestFactory.setConnectTimeout(props.getRestTemplateConfig().getConnectTimeout());
          clientHttpRequestFactory.setReadTimeout(props.getRestTemplateConfig().getReadTimeout());
          clientHttpRequestFactory.setBufferRequestBody(false);
          return clientHttpRequestFactory;
     }
}