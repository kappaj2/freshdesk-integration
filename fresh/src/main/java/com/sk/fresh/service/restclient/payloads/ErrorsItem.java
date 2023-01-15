package com.sk.fresh.service.restclient.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ErrorsItem {

     @JsonProperty("code")
     private String code;

     @JsonProperty("field")
     private String field;

     @JsonProperty("additional_info")
     private AdditionalInfo additionalInfo;

     @JsonProperty("message")
     private String message;
}