package com.sk.crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import com.sk.crm.dto.CustomerDTO;
import com.sk.crm.exception.CustomerNotFoundException;
import com.sk.crm.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController {

     private final CustomerService customerService;

     public CustomerController(CustomerService customerService) {
          this.customerService = customerService;
     }

     @GetMapping("/{customer-id}")
     public Mono<ResponseEntity<CustomerDTO>> findCustomerById(@PathVariable("customer-id") int customerId) throws CustomerNotFoundException {
          log.info("Searching for customer with id : {}", customerId);
          return customerService.findCustomerById(customerId).map(ResponseEntity::ok)
                  .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
     }

     @PostMapping
     public Mono<ResponseEntity<CustomerDTO>> createCustomer(@RequestBody CustomerDTO customerDTO,
                                                             UriComponentsBuilder uriComponentsBuilder,
                                                             ServerHttpRequest req) {
          log.info("Creating customer with dto payload : {}", customerDTO);
          return customerService.createCustomer(customerDTO)
                  .map(cus -> ResponseEntity.created(uriComponentsBuilder.path(req.getPath() + "/{customer-id}").build(cus.getId())).build());
     }

     @PutMapping("/{customer-id}")
     public Mono<ResponseEntity<CustomerDTO>> updateCustomer(@PathVariable("customer-id") int customerId,
                                                             @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
          log.info("Updating customer id : {} with payload : {}", customerId, customerDTO);
          return customerService.updateCustomer(customerId, customerDTO)
                  .map(cus -> ResponseEntity.ok(cus));
     }

}
