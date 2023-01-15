package com.sk.crm.repository.entity;

/**
 * Enum to define the different message types we are sending to Freshdesk.
 */
public enum FreshMessageType {

     CUSTOMER_CREATION("CUSTOMER_CREATION"),
     CUSTOMER_UPDATE("CUSTOMER_UPDATE"),
     AGENT_CREATION("AGENT_CREATION"),
     AGENT_UPDATE("AGENT_UPDATE");

     private String messageType;

     FreshMessageType(String messageType) {
          this.messageType = messageType;
     }
}
