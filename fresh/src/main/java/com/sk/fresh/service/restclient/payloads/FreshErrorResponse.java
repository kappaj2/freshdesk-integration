package com.sk.fresh.service.restclient.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshErrorResponse implements FreshDeskResponsePayload {

     @JsonProperty("description")
     private String description;

     @JsonProperty("errors")
     private List<ErrorsItem> errors;

     @JsonProperty("response-code")
     private Integer responseCode;

}