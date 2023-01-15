package com.sk.fresh.service.crm.webclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CRMCountry {

     private String countryCode;
     private String countryDisplayName;

}

