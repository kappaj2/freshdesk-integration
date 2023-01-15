package com.sk.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AgentDTO {

     @JsonProperty("agent-id")
     private int agentId;

     @JsonProperty("name")
     private String name;

     @JsonProperty("surname")
     private String surname;

     @JsonProperty("email-address")
     private String emailAddress;
}
