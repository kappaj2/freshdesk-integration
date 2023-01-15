package com.sk.fresh.aws.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

     private Integer id;

     @JsonProperty("name")
     private String name;

     @JsonProperty("surname")
     private String surname;

     @JsonProperty("cell-number")
     private String cellNumber;

     @JsonProperty("id-number")
     private String idNumber;

     @JsonProperty("email")
     private String email;

     @Enumerated(EnumType.STRING)
     @JsonProperty("customer-status")
     private CustomerStatusEnum customerStatus;

     @JsonProperty("country-code")
     private String countryCode;
}