package com.sk.fresh.controller;

import com.sk.fresh.service.freshdesk.ContactManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fresh/contacts")
@Slf4j
@RequiredArgsConstructor
public class ContactsController {

     private final ContactManagementService contactManagementService;

     @GetMapping
     public ResponseEntity<?> getListContacts() {

          log.info("Retrieving list of all FreshDesk contacts.");

          var freshContactViewList = contactManagementService.retrieveListOfContacts();
          return ResponseEntity.ok(freshContactViewList);
     }

     @GetMapping("/email/{email-address}")
     public ResponseEntity<?> retrieveContactByEmail(@PathVariable("email-address") String emailAddress) {

          var freshContactViewOpt = contactManagementService.retrieveContactByEmailAddress(emailAddress);
          if (freshContactViewOpt.isPresent()) {
               return ResponseEntity.ok(freshContactViewOpt.get());
          }
          return new ResponseEntity(HttpStatus.NOT_FOUND);
     }

     @GetMapping("/msisdn/{msisdn}")
     public ResponseEntity<?> retrieveContactByMsisdn(@PathVariable("msisdn") String msisdn) {

          var freshContactViewOpt = contactManagementService.retrieveContactByMsisdn(msisdn);
          if (freshContactViewOpt.isPresent()) {
               return ResponseEntity.ok(freshContactViewOpt.get());
          }
          return new ResponseEntity(HttpStatus.NOT_FOUND);
     }

     @GetMapping("/id/{customer_id}")
     public ResponseEntity<?> retrieveContactByExternalId(@PathVariable("customer_id") String customer_id) {

          var freshContactViewOpt = contactManagementService.retrieveContactByExternalId(customer_id);
          if (freshContactViewOpt.isPresent()) {
               return ResponseEntity.ok(freshContactViewOpt.get());
          }
          return new ResponseEntity(HttpStatus.NOT_FOUND);
     }

     @DeleteMapping
     public ResponseEntity<?> deleteAllContacts() {
          contactManagementService.deleteAllContacts();
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }

}
