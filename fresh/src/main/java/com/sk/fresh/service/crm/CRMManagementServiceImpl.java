package com.sk.fresh.service.crm;

import com.sk.fresh.service.crm.webclient.CRMWebClientHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import com.sk.fresh.service.crm.webclient.dto.CRMCountry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class CRMManagementServiceImpl implements CRMManagementService {

     private static Map<String, CRMCountry> countriesMap = new HashMap<>();
     private StampedLock stampedLock = new StampedLock();
     private final CRMWebClientHandler crmWebClientHandler;

     @Override
     public Optional<CRMCountry> retrieveCountryForAlpha2Code(String alpha2Code) {
          log.debug("Retrieving county for code : {}", alpha2Code);
          if (countriesMap.size() == 0 || !countriesMap.containsKey(alpha2Code)) {
               log.info("Retry to fetch CountriesMap from crm module. Current map size : {}", countriesMap.size());
               var stamp = stampedLock.readLock();
               try {
                    populateCountriesMap();
               } finally {
                    stampedLock.unlockRead(stamp);
               }
          }
          return Optional.ofNullable(countriesMap.get(alpha2Code));
     }

     private void populateCountriesMap() {
          log.info("Retrieving list of countries from CRM module");
          var crmCountriesEndpoint = "/api/v1/countries";
          ParameterizedTypeReference<List<CRMCountry>> parameterizedTypeReference =
                  new ParameterizedTypeReference<>() {
                  };

          var countriesListMono
                  = crmWebClientHandler.fetchList(parameterizedTypeReference, builder -> builder.path(crmCountriesEndpoint).build());

          var countryList = countriesListMono.block();
          log.info("Country list is : {}", countryList);
          countryList.forEach(country -> countriesMap.put(country.getCountryCode(), country));
     }
}
