package com.sk.crm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sk.crm.dto.CountryDTO;
import com.sk.crm.service.CountryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {

     private static Map<String, CountryDTO > countriesMap = new HashMap<>();

     static  {
          String[] isoCountries = Locale.getISOCountries();
          Arrays.stream(isoCountries)
                  .forEach( country ->{

                       Locale locale = new Locale("", country);
                       CountryDTO dto = new CountryDTO();
                       dto.setCountryCode(locale.getISO3Country());
                       dto.setCountryDisplayName(locale.getDisplayName());
                       countriesMap.put(locale.getISO3Country(), dto);
                  });
     }

     @Override
     public CountryDTO getCountryDetails(String countryCode) {
          return countriesMap.get(countryCode.toUpperCase().trim());
     }

     @Override
     public List<CountryDTO> getCountries(){
          List<CountryDTO> countryDTOList = new ArrayList<>();
          countriesMap.entrySet().stream()
                  .forEach(entry-> {
                       countryDTOList.add(entry.getValue());
                  });
          return countryDTOList;
     }
}
