package com.sk.fresh.repository;

public enum MessageStatus {

     CONTACT_CREATED("CONTACT_CREATED"),
     CONTACT_UPDATED("CONTACT_UPDATED"),
     PENDING_PROCESSING("PENDING_PROCESSING"),
     FRESH_EXCEPTION("FRESH_EXCEPTION"),
     SYSTEM_EXCEPTION("SYSTEM_EXCEPTION"),
     REACHED_RETRY_LIMIT("REACHED_RETRY_LIMIT"),
     CUSTOMER_NOT_ACTIVE("CUSTOMER_NOT_ACTIVE");

     private String messageStatus;

     MessageStatus(String messageStatus) {
          this.messageStatus = messageStatus;
     }
}
