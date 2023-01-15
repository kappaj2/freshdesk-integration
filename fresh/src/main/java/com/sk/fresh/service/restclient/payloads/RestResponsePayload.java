package com.sk.fresh.service.restclient.payloads;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RestResponsePayload implements FreshDeskResponsePayload {
     private int responseCode;
     private String responseMessage;

     public RestResponsePayload(int responseCode, String responseMessage) {
          this.responseCode = responseCode;
          this.responseMessage = responseMessage;
     }
}