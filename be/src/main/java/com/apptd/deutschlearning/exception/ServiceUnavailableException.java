package com.apptd.deutschlearning.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends ApiException {
  public ServiceUnavailableException(String errorCode, String message) {
    super(HttpStatus.SERVICE_UNAVAILABLE, errorCode, message);
  }
}

