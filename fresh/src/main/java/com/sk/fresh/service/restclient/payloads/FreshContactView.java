package com.sk.fresh.service.restclient.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshContactView implements FreshDeskRequestPayload {

     @JsonProperty("id")
     private String id;

     @JsonProperty("name")
     private String name;

     @JsonProperty("email")
     private String email;

     @JsonProperty("phone")
     private String phone;

     @JsonProperty("mobile")
     private String mobile;

     @JsonProperty("unique_external_id")
     private String unique_external_id;

     @JsonProperty("address")
     private String address;

     @JsonProperty("language")
     private String language;

     @JsonProperty("description")
     private String description;

     @JsonProperty("deleted")
     private String deleted;

     @JsonProperty("active")
     private String active;

     @JsonProperty("company_id")
     private String companyId;
}
