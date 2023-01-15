package com.sk.crm.service;

import com.sk.crm.dto.CountryDTO;

import java.util.List;

public interface CountryService {

     CountryDTO getCountryDetails(String countryCode);

     List<CountryDTO> getCountries();
}
