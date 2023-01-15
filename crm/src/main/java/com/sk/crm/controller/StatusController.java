package com.sk.crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/status")
@Slf4j
public class StatusController {

     @GetMapping
     public ResponseEntity<?> getStatus(){
          log.info("Received status call!");
          return ResponseEntity.ok("I'm alive!");
     }
}
