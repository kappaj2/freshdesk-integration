package com.sk.crm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.sk.crm.aws.queue.QueueSubmitterService;
import com.sk.crm.dto.CustomerDTO;
import com.sk.crm.dto.CustomerStatusEnum;
import com.sk.crm.exception.CustomerNotFoundException;
import com.sk.crm.repository.CustomerRepository;
import com.sk.crm.repository.entity.Customer;
import com.sk.crm.service.CustomerService;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

     private final CustomerRepository customerRepository;
     private final ModelMapper modelMapper;
     private final QueueSubmitterService queueSubmitterService;

     @Override
     public Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO) {
          log.info("Doing customer create with : {}", customerDTO);

          var customer = modelMapper.map(customerDTO, Customer.class);
          customer.setCustomerStatusEnum(CustomerStatusEnum.PENDING_VALIDATION);
          customer.setDateCreated(new Date());
          customer.setCountryCode(customerDTO.getCountryCode());
          Customer savedCustomer = customerRepository.save(customer);

          var saveCustomerDTO = modelMapper.map(savedCustomer, CustomerDTO.class);
          /*
               Submit create notification to AWS
           */
          queueSubmitterService.submitCreateMessage(saveCustomerDTO);

          return Mono.just(saveCustomerDTO);
     }

     @Override
     public Mono<CustomerDTO> updateCustomer(int customerId, CustomerDTO customerDTO) throws CustomerNotFoundException {
          log.info("Doing updateCustomer with customerId {} and dto : {}", customerId, customerDTO);

          var customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Cannot find customer for id : " + customerId));

          customer.setCellNumber(customerDTO.getCellNumber());
          customer.setCustomerStatusEnum(customerDTO.getCustomerStatus());
          customer.setIdNumber(customerDTO.getIdNumber());
          customer.setEmail(customerDTO.getEmail());
          customer.setCountryCode(customerDTO.getCountryCode());
          customer.setDataModified(new Date());
          Customer updatedCustomer = customerRepository.save(customer);

          /*
               Submit customer update notification to AWS
           */
          var updatedCustomerDTO = modelMapper.map(updatedCustomer, CustomerDTO.class);
          queueSubmitterService.submitUpdateMessage(updatedCustomerDTO);

          return Mono.just(modelMapper.map(updatedCustomer, CustomerDTO.class));
     }

     @Override
     public Mono<CustomerDTO> findCustomerById(Integer customerId) throws CustomerNotFoundException {
          log.info("Doing customer find by id : {}", customerId);

          var customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Cannot find customer for id : " + customerId));

          return Mono.just(modelMapper.map(customer, CustomerDTO.class));
     }
}
