package com.sk.crm.exception;

public class CustomerNotFoundException extends Exception {
     public CustomerNotFoundException(String message) {
          super(message);
     }

     public CustomerNotFoundException(String message, Throwable cause) {
          super(message, cause);
     }
}
