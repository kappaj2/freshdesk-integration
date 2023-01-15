package com.sk.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import com.sk.crm.dto.CustomerDTO;
import com.sk.crm.service.CustomerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CustomerController.class)
public class CustomerControllerTest {

     @Autowired
     private ApplicationContext context;
     private WebTestClient webTestClient;

     @MockBean
     private CustomerService customerService;

     @BeforeEach
     void setUp() {
          webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/").build();
     }

     @Test
     @DisplayName("Testing customer retrieval")
     public void testRetrievingOfCustomerUsingId_returnsCorrect() throws Exception {

          var dto = new CustomerDTO();
          dto.setId(1);
          dto.setName("Bennie");

          var just = Mono.just(dto);
          Mockito.when(customerService.findCustomerById(any())).thenReturn(just);

          var rs = webTestClient.get().uri("/api/v1/customers/1").exchange();
          rs.expectStatus().isOk()
                  .expectBody(CustomerDTO.class)
                  .consumeWith(result -> {
                       CustomerDTO customerDTO = result.getResponseBody();
                       assertThat(customerDTO).isNotNull();
                       assertThat(customerDTO.getName()).isEqualTo("Bennie");
                  });
     }

     @Test
     public void ensureThat_getUserById_invalidId_resultsInError() throws Exception {
          var invalidId = -1;
          Mockito.when(customerService.findCustomerById(invalidId)).thenReturn(Mono.empty());

          var rs = webTestClient.get().uri("/api/v1/customers/" + invalidId).exchange();

          rs.expectStatus().isNotFound();
     }

     @Test
     @DisplayName("Testing customer creation")
     public void testCustomerCreation_resultsInSuccess() throws Exception {

          var custDto = new CustomerDTO();
          custDto.setName("Bennie");
          custDto.setSurname("Boekwurm");
          custDto.setId(100);
          custDto.setCountryCode("ZAF");

          Mockito.when(customerService.createCustomer(custDto)).thenReturn(Mono.just(custDto));

          var rs = webTestClient.post().uri("/api/v1/customers")
                  .body(BodyInserters.fromValue(custDto))
                  .exchange();

          rs.expectStatus().isCreated().expectHeader()
                  .valueMatches("LOCATION", "^/api/v1/customers/\\d+");

     }

     @Test
     @DisplayName("Testing customer update")
     public void testCustomerUpdate_resultsInSuccess() throws Exception {

          var custDto = new CustomerDTO();
          custDto.setName("Bennie");
          custDto.setSurname("Boekwurm");
          custDto.setCellNumber("1111111111");
          custDto.setId(100);
          custDto.setCountryCode("ZAF");

          Mockito.when(customerService.updateCustomer(10, custDto)).thenReturn(Mono.just(custDto));

          var rs = webTestClient.put().uri("/api/v1/customers/10")
                  .body(BodyInserters.fromValue(custDto))
                  .exchange();

          rs.expectStatus().isOk()
                  .expectBody(CustomerDTO.class)
                  .consumeWith(result -> {
                       CustomerDTO customerDTO = result.getResponseBody();
                       assertThat(customerDTO).isNotNull();
                       assertThat(customerDTO.getId() == 10);
                       assertThat(customerDTO.getCellNumber()).isEqualTo(custDto.getCellNumber());
                  });
     }
}