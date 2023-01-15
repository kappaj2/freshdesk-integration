package com.sk.fresh.exception;

public class CustomerNotActiveException extends Exception {

     public CustomerNotActiveException(String message) {
          super(message);
     }

     public CustomerNotActiveException(String message, Throwable cause) {
          super(message, cause);
     }
}
