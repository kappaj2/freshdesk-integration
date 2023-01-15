package com.sk.fresh.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sk.fresh.service.crm.CRMManagementService;
import com.sk.fresh.service.crm.webclient.dto.CRMCountry;

@RestController
@Slf4j
@RequestMapping("/api/v1/crm/test/country")
@RequiredArgsConstructor
public class TestCRMController {

     private final CRMManagementService crmManagementService;

     @GetMapping("/{country-code}")
     public ResponseEntity<CRMCountry> testGetCountryCode(@PathVariable("country-code") String countryCode) {
          var crmCountry = crmManagementService.retrieveCountryForAlpha2Code(countryCode);
          return ResponseEntity.ok(crmCountry.get());
     }
}
