package com.sk.crm.service;

import com.sk.crm.dto.CustomerDTO;
import reactor.core.publisher.Mono;
import com.sk.crm.exception.CustomerNotFoundException;

public interface CustomerService {

     Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO);

     Mono<CustomerDTO> updateCustomer(int customerId, CustomerDTO customerDTO) throws CustomerNotFoundException;

     Mono<CustomerDTO> findCustomerById(Integer customerId) throws CustomerNotFoundException;

}
