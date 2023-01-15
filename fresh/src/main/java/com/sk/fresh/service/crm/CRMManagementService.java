package com.sk.fresh.service.crm;

import com.sk.fresh.service.crm.webclient.dto.CRMCountry;

import java.util.Optional;

public interface CRMManagementService {
     Optional<CRMCountry> retrieveCountryForAlpha2Code(String alpha2Code);
}
