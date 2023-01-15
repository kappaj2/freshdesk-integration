package com.sk.fresh.aws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sk.fresh.repository.FreshMessageType;

import java.util.Date;

@Data
@NoArgsConstructor
public class FreshDeskPayload {

     @JsonProperty("fresh-message-type")
     private FreshMessageType freshMessageType;

     @JsonProperty("date-created")
     private Date dateCreated;

     @JsonProperty("message-payload")
     private String messagePayload;

     @JsonProperty("message-uuid")
     private String messageUUID;
}