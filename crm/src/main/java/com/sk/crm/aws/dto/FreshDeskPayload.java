package com.sk.crm.aws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import com.sk.crm.repository.entity.FreshMessageType;

import java.util.Date;

@Data
@Builder
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
