package com.sk.crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.sk.crm.dto.CountryDTO;
import com.sk.crm.service.CountryService;

@RestController
@Slf4j
@RequestMapping("/api/v1/countries")
public class CountryController {

     private final CountryService countryService;

     public CountryController(CountryService countryService) {
          this.countryService = countryService;
     }

     @GetMapping("/{country-code}")
     public Mono<ResponseEntity<CountryDTO>> getCountryDetails(@PathVariable("country-code") String countryCode) {
          log.info("Retrieving country details for code : {}", countryCode);
          var dto = countryService.getCountryDetails(countryCode);
          return Mono.just(ResponseEntity.ok(dto));
     }

     @GetMapping
     public Flux<CountryDTO> getCountries(){
          log.info("Received call for getCountries");
          return Flux.fromStream(countryService.getCountries().stream());
     }
}
