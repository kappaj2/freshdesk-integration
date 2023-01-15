package com.sk.fresh.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@Slf4j
public class WebClientConfig {

     @Value("${cloud.aws.apigateway.backend-url}")
     private String sfhBackendUrl;
     @Value("${spring.security.oauth2.client.provider.sfh-apigw.token-uri}")
     private String tokenUri;
     @Value("${spring.security.oauth2.client.registration.sfh-apigw.client-id}")
     private String clientId;
     @Value("${spring.security.oauth2.client.registration.sfh-apigw.client-secret}")
     private String clientSecret;

     @Bean
     public OAuth2AuthorizedClientRepository authorizedClientRepository(
             OAuth2AuthorizedClientService authorizedClientService) {
          return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
     }

     @Bean
     public ReactiveClientRegistrationRepository clientRegistrations() {
          var registration = ClientRegistration
                  .withRegistrationId("sfh-apigw")
                  .tokenUri(tokenUri)
                  .clientId(clientId)
                  .clientSecret(clientSecret)
                  .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                  //  .scope(scope)
                  .build();
          return new InMemoryReactiveClientRegistrationRepository(registration);
     }

     public class CustomUriBuilderFactory extends DefaultUriBuilderFactory {

          public CustomUriBuilderFactory(String baseUriTemplate) {
               super(UriComponentsBuilder.fromHttpUrl(baseUriTemplate));
               super.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
          }
     }

     /**
      * Create a new WebClient bean using the OAuth2 configuration. This will inject the JWT by default when doing the outgoing call.
      * The default properties are configured to use AWS API Gateway with cognito combination using ClientCredentials based auth flow.
      *
      * @param clientRegistrations
      * @return
      */
     @Bean(name = "sfh-webclient")
     public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
          var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                  clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
          oauth.setDefaultClientRegistrationId("sfh-apigw");
          return WebClient.builder()
                  .uriBuilderFactory(new CustomUriBuilderFactory(
                          sfhBackendUrl
                  ))
                  .filter(oauth)
                  .filter(logRequest())
                  .filter(logResponse())
                  .build();
     }

     /**
      * Log request details for the downstream web service calls
      */
     private ExchangeFilterFunction logRequest() {
          return ExchangeFilterFunction.ofRequestProcessor(c -> {
               log.info("Request: {} {}", c.method(), c.url());
               c.headers().forEach((n, v) -> {
                    if (!n.equalsIgnoreCase(AUTHORIZATION)) {
                         log.info("request header {}={}", n, v);
                    } else {
                         // as the AUTHORIZATION header is something security bounded
                         // will show up when the debug level logging is enabled
                         // for example using property - logging.level.root=DEBUG
                         log.info("request header {}={}", n, v);
                    }
               });
               return Mono.just(c);
          });
     }

     /**
      * Log response details for the downstream web service calls
      */
     private ExchangeFilterFunction logResponse() {
          return ExchangeFilterFunction.ofResponseProcessor(c -> {
               log.info("Response: {}", c.statusCode());
               // if want to show the response headers in the log by any chance?
                 /*c.headers().asHttpHeaders().forEach((n, v) -> {
                     testWebClientLogger.info("response header {}={}", n, v);
                 });*/
               return Mono.just(c);
          });
     }
}
