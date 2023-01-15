package com.sk.fresh.aws.dto;

public enum CustomerStatusEnum {

     PENDING_VALIDATION("PENDING_VALIDATION"),
     ACTIVE("ACTIVE"),
     SUSPENDED("SUSPENDED"),
     DELETED("DELETED");

     private String status;

     CustomerStatusEnum(String status) {
          this.status = status;
     }
}
