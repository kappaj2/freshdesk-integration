package com.sk.fresh.service.restclient.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class AdditionalInfo {

     @JsonProperty("user_id")
     private long userId;
}